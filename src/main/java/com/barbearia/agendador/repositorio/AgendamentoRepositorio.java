package com.barbearia.agendador.repositorio;

import com.barbearia.agendador.modelo.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepositorio extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByProfissionalId(Long profissionalId);
}
