package com.barbearia.agendador.seguranca;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracaoDeSeguranca {

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, FiltroDeToken filtroDeToken) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requisicoes -> requisicoes
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(tratamento -> tratamento
                        .authenticationEntryPoint((requisicao, resposta, erro) ->
                                resposta.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .addFilterBefore(filtroDeToken, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager gerenciadorDeAutenticacao(AuthenticationConfiguration configuracao) throws Exception {
        return configuracao.getAuthenticationManager();
    }
}
