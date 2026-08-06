package com.otavio.agendador.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "O cliente é obrigatório")
    private Cliente cliente;

    @ManyToOne
    @NotNull(message = "O profissional é obrigatório")
    private Profissional profissional;

    @ManyToOne
    @NotNull(message = "O serviço é obrigatório")
    private Servico servico;

    @NotNull(message = "O horário de início é obrigatório")
    private LocalDateTime inicio;

    public Agendamento() {
    }

    public Agendamento(Cliente cliente, Profissional profissional, Servico servico, LocalDateTime inicio) {
        this.cliente = cliente;
        this.profissional = profissional;
        this.servico = servico;
        this.inicio = inicio;
    }

    // Não é uma coluna no banco: é calculado a partir do inicio + duração do serviço.
    // Usamos isso pra checar conflito de horário sem precisar guardar o fim duplicado.
    @Transient
    public LocalDateTime getFim() {
        return inicio.plusMinutes(servico.getDuracaoEmMinutos());
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }
}
