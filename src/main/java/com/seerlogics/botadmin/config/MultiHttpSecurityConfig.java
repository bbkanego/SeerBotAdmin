package com.seerlogics.botadmin.config;

import com.lingoace.spring.authentication.JWTAuthenticationFilter;
import com.lingoace.spring.authentication.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Created by bkane on 11/4/18.
 */
@EnableWebSecurity
public class MultiHttpSecurityConfig {
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Order(1)
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Resource(name = "accountService")
        private UserDetailsService userDetailsService;

        @Autowired
        private AppProperties appProperties;

        @Bean
        public PasswordEncoder getPasswordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        JWTAuthenticationFilter authenticationTokenFilterBean() {
            JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
            jwtAuthenticationFilter.setUserDetailsService(this.userDetailsService);
            jwtAuthenticationFilter.setSecretKey(appProperties.getJwtSecretKey());
            return jwtAuthenticationFilter;
        }

        RestAuthenticationEntryPoint unauthorizedHandler() {
            return new RestAuthenticationEntryPoint();
        }

        /**
         * ******* The below is VERY important. The below means that we are setting "security='none'" for the below
         * URL patterns. The Spring Security interceptor will basically just ignore the below URLs.
         */
        @Override
        public void configure(WebSecurity web) throws Exception {
            // "/**" will allow ALL requests.
            web.ignoring().antMatchers("/api/**/generate-token", "/error", "/metadata/validation/**",
                    "/api/v1/account/signup", "/metadata/messages", "/actuator/**",
                    "/api/v1/account/init/**", "/api/cms/all-content",
                    // swagger related resources
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/v2/api-docs",
                    "/webjars/**",
                    // swagger related resources ends
                    "/api/v1/subscription/init/**", "/api/v1/subscription/signup");
        }

        /**
         * https://www.devglan.com/spring-security/jwt-role-based-authorization
         * <p>
         * For ALL other URLs which are not ignored, the below will authenticate access!
         *
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.headers().frameOptions().sameOrigin();
            /**
             * We are disabling CSRF here since we are using JWT to authenticate. We are not using "Cookie" for auth.
             * CSRF is possible when using "Cookie" method for auth.
             * The cors() method will add the Spring-provided CorsFilter to the application context which in
             * turn bypasses the authorization checks for OPTIONS requests.
             */
            http.cors().and().csrf().disable()
                    // https://www.baeldung.com/spring-security-cors-preflight#secure
                    .authorizeRequests()
                    /**
                     * Here, we have configured that no authentication is required to access the url /token, /signup
                     * and rest of the urls are secured. Here prePostEnabled = true enables support for method
                     * level security and enables use of @PreAuthorize
                     */
                    .anyRequest().authenticated()
                    .and()
                    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler()).and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    //.addFilterBefore(new JoltResponseTransformationFilter(joltSpecs), UsernamePasswordAuthenticationFilter.class)
                    .addFilterAt(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        }
    }
}
