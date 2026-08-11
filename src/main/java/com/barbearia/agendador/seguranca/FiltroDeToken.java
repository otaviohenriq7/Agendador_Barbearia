package com.barbearia.agendador.seguranca;

import com.barbearia.agendador.modelo.Usuario;
import com.barbearia.agendador.repositorio.UsuarioRepositorio;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class FiltroDeToken extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final TokenService tokenService;
    private final UsuarioRepositorio usuarioRepositorio;

    public FiltroDeToken(TokenService tokenService, UsuarioRepositorio usuarioRepositorio) {
        this.tokenService = tokenService;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao,
                                    HttpServletResponse resposta,
                                    FilterChain proximoFiltro) throws ServletException, IOException {

        String cabecalho = requisicao.getHeader("Authorization");

        if (cabecalho != null && cabecalho.startsWith(PREFIXO)) {
            autenticar(cabecalho.substring(PREFIXO.length()));
        }

        proximoFiltro.doFilter(requisicao, resposta);
    }

    private void autenticar(String token) {
        try {
            String login = tokenService.extrairLogin(token);
            Optional<Usuario> usuario = usuarioRepositorio.findByLogin(login);

            usuario.ifPresent(encontrado -> SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(encontrado, null, encontrado.getAuthorities())));

        } catch (JwtException erro) {
            SecurityContextHolder.clearContext();
        }
    }
}
