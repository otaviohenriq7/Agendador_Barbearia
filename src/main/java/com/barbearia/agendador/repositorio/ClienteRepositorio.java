package com.barbearia.agendador.repositorio;

import com.barbearia.agendador.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
}
