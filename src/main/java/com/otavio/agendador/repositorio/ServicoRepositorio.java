package com.otavio.agendador.repositorio;

import com.otavio.agendador.modelo.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepositorio extends JpaRepository<Servico, Long> {
}
