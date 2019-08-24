package com.seerlogics.botadmin.controller;

import com.lingoace.dto.AuthToken;
import com.lingoace.dto.Policy;
import com.lingoace.dto.Role;
import com.lingoace.dto.Statement;
import com.lingoace.model.Login;
import com.lingoace.spring.authentication.JWTTokenProvider;
import com.seerlogics.botadmin.config.AppProperties;
import com.seerlogics.commons.model.CustomUserDetails;
import com.seerlogics.commons.model.Organization;
import com.seerlogics.commons.model.Party;
import com.seerlogics.commons.model.Person;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by bkane on 11/4/18.
 */
@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppProperties appProperties;

    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public String doLogin() {
        return "{success: 'true'}";
    }

    @PostMapping(value = "/generate-token")
    public ResponseEntity register(@RequestBody Login loginUser) throws AuthenticationException {

        /**
         * Authenticate the user using the username and password
         */
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUserName(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        final String token = JWTTokenProvider.generateToken(authentication,
                SignatureAlgorithm.forName(appProperties.getJwtSignatureAlgo()),
                appProperties.getJwtSecretKey(), appProperties.getJwtTtl());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        AuthToken authToken = new AuthToken(token);
        for (GrantedAuthority authority : authorities) {

        }
        authToken.setUserName(loginUser.getUserName());
        Party party = customUserDetails.getAccount().getOwner();
        if (party instanceof Person) {
            Person person = (Person) party;
            authToken.setFirstName(person.getFirstName());
            authToken.setLastName(person.getLastName());
        } else if (party instanceof Organization) {
            Organization organization = (Organization) party;
            authToken.setFirstName(organization.getName());
        }

        customUserDetails.getAccount().getRoles().forEach(dbRole -> {
            Role roleDTO = new Role();
            roleDTO.setCode(dbRole.getCode());
            roleDTO.setName(dbRole.getName());
            authToken.getRoles().add(roleDTO);

            dbRole.getPolicies().forEach(dbPolicy -> {
                Policy policyDTO = new Policy();
                roleDTO.getPolicies().add(policyDTO);
                policyDTO.setName(policyDTO.getName());
                dbPolicy.getStatements().forEach(dbStatement -> {
                    Statement statementDTO = new Statement();
                    statementDTO.setEffect(dbStatement.getEffect());
                    policyDTO.getStatements().add(statementDTO);
                    statementDTO.setResource(dbStatement.getResource().getName());
                    dbStatement.getActions().forEach(dbAction -> {
                        statementDTO.getActions().add(dbAction.getName());
                    });
                });
            });
        });

        return ResponseEntity.ok(authToken);
    }
}
