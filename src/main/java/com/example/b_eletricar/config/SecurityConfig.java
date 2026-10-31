package com.example.b_eletricar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.b_eletricar.security.CustomOidcUser;
import com.example.b_eletricar.security.LoginFailureHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOidcUser customOidcUserService;
    private final LoginFailureHandler loginFailureHandler;

    public SecurityConfig(CustomOidcUser customOidcUserService,
                          LoginFailureHandler loginFailureHandler) {
        this.customOidcUserService = customOidcUserService;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/perfil", true)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOidcUserService)
                )
                .defaultSuccessUrl("/perfil", true)
                .failureUrl("/login?erroGoogle")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error", "/login", "/cadastro",
                                 "/css/**", "/js/**", "/images/**", "/webjars/**",
                                 "/veiculos/listagem").permitAll()
                .requestMatchers(HttpMethod.GET, "/veiculos/{id}", "/veiculos/{id}/fornecedor").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}