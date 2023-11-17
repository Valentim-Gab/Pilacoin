package br.ufsm.csi.pilacoin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(
            authorizeConfig -> {
              authorizeConfig.requestMatchers("/pilacoin/**").permitAll();
              authorizeConfig.requestMatchers("/user/**").permitAll();
              authorizeConfig.anyRequest().authenticated();
            })
        .exceptionHandling(
            exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                (request, response, authException) -> {
                  response.getWriter().println("Acesso negado!");
                  response.setStatus(HttpStatus.FORBIDDEN.value());
                }))
        .build();
  }
}
