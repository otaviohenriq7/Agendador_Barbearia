package com.barbearia.agendador.seguranca;

import com.barbearia.agendador.modelo.Usuario;
import com.barbearia.agendador.repositorio.UsuarioRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioInicial.class);

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder codificadorDeSenha;
    private final String login;
    private final String senha;

    public UsuarioInicial(UsuarioRepositorio usuarioRepositorio,
                          PasswordEncoder codificadorDeSenha,
                          @Value("${agendador.usuario-inicial.login}") String login,
                          @Value("${agendador.usuario-inicial.senha}") String senha) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorDeSenha = codificadorDeSenha;
        this.login = login;
        this.senha = senha;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepositorio.count() > 0) {
            return;
        }

        usuarioRepositorio.save(new Usuario(login, codificadorDeSenha.encode(senha)));
        log.warn("Usuario inicial '{}' criado. Troque a senha antes de colocar em producao.", login);
    }
}
