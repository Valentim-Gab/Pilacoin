package br.ufsm.csi.pilacoin.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        authorizeConfig -> {
                            authorizeConfig.requestMatchers(HttpMethod.GET, "/pilacoin/**").permitAll();
                            authorizeConfig.requestMatchers(HttpMethod.POST, "/pilacoin/transfer").permitAll();
                            authorizeConfig.requestMatchers(HttpMethod.GET, "/user/**").permitAll();
                            authorizeConfig.requestMatchers("/ws/**").permitAll();
                            authorizeConfig.requestMatchers("/update/**").permitAll();
                            authorizeConfig.requestMatchers("/error/**").permitAll();
                            authorizeConfig.anyRequest().authenticated();
                        })
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.getWriter().println(authException.getMessage());
                                    System.out.println("Authentication failed for: " + request.getRequestURI());
                                    System.out.println("Exception message: " + authException.getMessage());
                                    response.setStatus(HttpStatus.FORBIDDEN.value());
                                }))
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("http://localhost:3000"));
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
                    configuration.setAllowedHeaders(List.of("*"));

                    return configuration;
                }))
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
