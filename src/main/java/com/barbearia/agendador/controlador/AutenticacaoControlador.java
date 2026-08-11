package com.barbearia.agendador.controlador;

import com.barbearia.agendador.modelo.Usuario;
import com.barbearia.agendador.requisicao.LoginRequisicao;
import com.barbearia.agendador.resposta.TokenResposta;
import com.barbearia.agendador.seguranca.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AutenticacaoControlador {

    private final AuthenticationManager gerenciadorDeAutenticacao;
    private final TokenService tokenService;

    public AutenticacaoControlador(AuthenticationManager gerenciadorDeAutenticacao, TokenService tokenService) {
        this.gerenciadorDeAutenticacao = gerenciadorDeAutenticacao;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public TokenResposta entrar(@Valid @RequestBody LoginRequisicao requisicao) {
        try {
            Authentication autenticacao = gerenciadorDeAutenticacao.authenticate(
                    new UsernamePasswordAuthenticationToken(requisicao.login(), requisicao.senha()));

            Usuario usuario = (Usuario) autenticacao.getPrincipal();

            return new TokenResposta(tokenService.gerar(usuario));

        } catch (BadCredentialsException erro) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login ou senha inválidos");
        }
    }
}
