package com.barbearia.agendador.repositorio;

import com.barbearia.agendador.modelo.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepositorio extends JpaRepository<Profissional, Long> {
}
