package com.otavio.agendador.repositorio;

import com.otavio.agendador.modelo.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepositorio extends JpaRepository<Profissional, Long> {
}
