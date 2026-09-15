package com.example.b_eletricar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD, jakarta.servlet.DispatcherType.ERROR).permitAll()
                
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                .requestMatchers("/h2-console/**").permitAll()

                .requestMatchers("/", "/veiculos", "/veiculos/filtrar", "/veiculos/{id}", 
                                 "/veiculos/proposta", "/veiculos/test-drive").permitAll()

                .requestMatchers("/admin/**", "/veiculos/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/veiculos", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/veiculos?logout")
                .permitAll()
            );

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/veiculos/proposta", "/veiculos/test-drive"));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}