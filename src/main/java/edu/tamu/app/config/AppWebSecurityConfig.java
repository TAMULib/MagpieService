package edu.tamu.app.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import edu.tamu.app.auth.service.AppUserDetailsService;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.weaver.auth.config.AuthWebSecurityConfig;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AppWebSecurityConfig extends AuthWebSecurityConfig<AppUser, AppUserRepo, AppUserDetailsService> {
	
	@Value("${app.security.allow-access}")
	 private String[] hosts;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .sessionManagement()
                .sessionCreationPolicy(STATELESS)
            .and()
                .authorizeRequests()
                    .expressionHandler(webExpressionHandler())
                    .antMatchers("/**/*")
                        .permitAll()
            .and()
                .headers()
                    .frameOptions()
                    .disable()
            .and()
            	.cors()
            .and()
                .csrf()
                    .disable()
            .addFilter(tokenAuthorizationFilter());
        // @formatter:on
    }
    
    

    @Override
    protected String buildRoleHierarchy() {
        StringBuilder roleHeirarchy = new StringBuilder();
        Role[] roles = Role.values();
        for (int i = 0; i <= roles.length - 2; i++) {
            roleHeirarchy.append(roles[i] + " > " + roles[i + 1]);
            if (i < roles.length - 2) {
                roleHeirarchy.append(" ");
            }
        }
        return roleHeirarchy.toString();
    }

}
