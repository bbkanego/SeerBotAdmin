package com.seerlogics.botadmin.service;

import com.lingoace.spring.service.BaseServiceImpl;
import com.seerlogics.commons.model.*;
import com.seerlogics.commons.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by bkane on 11/3/18.
 */
@Transactional
@Service(value = "accountService")
public class AccountService extends BaseServiceImpl<Account> implements UserDetailsService {

    // cannot do constructor injection since it causes cyclic dependency issue
    @Autowired
    private AccountRepository accountRepository;

    // cannot do constructor injection since it causes cyclic dependency issue
    @Autowired
    private PasswordEncoder passwordEncoder;

    // cannot do constructor injection since it causes cyclic dependency issue
    @Autowired
    private RoleService roleService;

    @Override
    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    public Collection<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyRole('ACCT_ADMIN', 'UBER_ADMIN')")
    public Account getSingle(Long id) {
        if (doesUserHaveAnyRole("UBER_ADMIN")) {
            return accountRepository.getOne(id);
        } else {
            String realm = getAuthenticatedUser().getRealm();
            return accountRepository.findByRealmAndId(realm, id);
        }
    }

    @Override
    public Account save(Account account) {
        // means that a new account is being created
        if (account.getId() == null) {
            account.setPassword(account.getPasswordCapture());
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Role selectedRole = (Role) account.getRoles().toArray()[0];
        Role matchingRole = roleService.findByCode(selectedRole.getCode());
        account.getRoles().clear();
        account.getRoles().add(matchingRole);
        return accountRepository.save(account);
    }

    @Override
    @PreAuthorize("hasAnyRole('UBER_ADMIN')")
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        Account account = accountRepository.findByUserName(userName);
        if (account == null) {
            throw new UsernameNotFoundException(userName);
        }
        return new CustomUserDetails(account.getUserName(), account.getPassword(),
                getAuthority(account), account);
    }

    public Account getAccountByUsername(String userName) {
        return accountRepository.findByUserName(userName);
    }

    private Set<GrantedAuthority> getAuthority(Account account) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        account.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
        });
        return authorities;
    }

    public Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getAccount();
    }

    public boolean doesUserHaveAnyRole(String... roles) {
        boolean hasRole = false;
        Set<String> authorities = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().
                getAuthentication().getAuthorities());
        for (String role : roles) {
            hasRole = authorities.contains("ROLE_" + role);
            if (hasRole) break;
        }
        return hasRole;
    }

    public Account initAccount(String type) {
        Account account = new Account();
        Party party = new Person();
        if ("organization".equals(type)) {
            party = new Organization();
        }
        Address address = new Address();
        party.addContactMode(address);
        account.setOwner(party);

        addReferenceData(account);

        return account;
    }

    private void addReferenceData(Account account) {
        List<Role> roles = new ArrayList<>();
        account.getReferenceData().put("roles", roles);
        Role acctAdmin = new Role();
        acctAdmin.setCode(Role.ROLE_TYPE.ACCT_ADMIN.name());
        acctAdmin.setName("Account Admin");
        roles.add(acctAdmin);
        Role acctUser = new Role();
        acctUser.setCode(Role.ROLE_TYPE.ACCT_USER.name());
        acctUser.setName("Account User");
        roles.add(acctUser);
        Role acctView = new Role();
        acctView.setCode(Role.ROLE_TYPE.ACCT_VIEW.name());
        acctView.setName("Account View");
        roles.add(acctView);
    }
}
