package com.otavio.agendador.repositorio;

import com.otavio.agendador.modelo.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepositorio extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByProfissionalId(Long profissionalId);
}
