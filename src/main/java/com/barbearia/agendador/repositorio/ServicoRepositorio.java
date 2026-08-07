package com.barbearia.agendador.repositorio;

import com.barbearia.agendador.modelo.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepositorio extends JpaRepository<Servico, Long> {
}
