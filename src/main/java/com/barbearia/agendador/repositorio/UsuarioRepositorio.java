package com.barbearia.agendador.repositorio;

import com.barbearia.agendador.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);
}
