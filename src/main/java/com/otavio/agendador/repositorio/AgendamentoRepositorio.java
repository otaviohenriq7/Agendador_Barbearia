package com.otavio.agendador.repositorio;

import com.otavio.agendador.modelo.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepositorio extends JpaRepository<Agendamento, Long> {

    // O Spring Data lê o nome do método e monta a query sozinho:
    // "por profissional (id)" vira "WHERE profissional_id = ?"
    List<Agendamento> findByProfissionalId(Long profissionalId);
}
