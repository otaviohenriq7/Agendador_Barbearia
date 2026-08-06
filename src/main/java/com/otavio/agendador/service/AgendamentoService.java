package com.otavio.agendador.service;

import com.otavio.agendador.modelo.Agendamento;
import com.otavio.agendador.modelo.Cliente;
import com.otavio.agendador.modelo.Profissional;
import com.otavio.agendador.modelo.Servico;
import com.otavio.agendador.repositorio.AgendamentoRepositorio;
import com.otavio.agendador.repositorio.ClienteRepositorio;
import com.otavio.agendador.repositorio.ProfissionalRepositorio;
import com.otavio.agendador.repositorio.ServicoRepositorio;
import com.otavio.agendador.requisicao.NovoAgendamentoRequisicao;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepositorio agendamentoRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final ProfissionalRepositorio profissionalRepositorio;
    private final ServicoRepositorio servicoRepositorio;

    public AgendamentoService(AgendamentoRepositorio agendamentoRepositorio,
                               ClienteRepositorio clienteRepositorio,
                               ProfissionalRepositorio profissionalRepositorio,
                               ServicoRepositorio servicoRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.profissionalRepositorio = profissionalRepositorio;
        this.servicoRepositorio = servicoRepositorio;
    }

    public Agendamento agendar(NovoAgendamentoRequisicao requisicao) {
        Cliente cliente = buscarClienteOuFalhar(requisicao.clienteId());
        Profissional profissional = buscarProfissionalOuFalhar(requisicao.profissionalId());
        Servico servico = buscarServicoOuFalhar(requisicao.servicoId());

        Agendamento novoAgendamento = new Agendamento(cliente, profissional, servico, requisicao.inicio());

        if (existeConflito(novoAgendamento)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esse profissional já tem um agendamento nesse horário");
        }

        return agendamentoRepositorio.save(novoAgendamento);
    }

    private boolean existeConflito(Agendamento novoAgendamento) {
        LocalDateTime novoInicio = novoAgendamento.getInicio();
        LocalDateTime novoFim = novoAgendamento.getFim();

        List<Agendamento> agendamentosDoProfissional =
                agendamentoRepositorio.findByProfissionalId(novoAgendamento.getProfissional().getId());

        // Dois intervalos se sobrepõem quando um começa antes do outro terminar,
        // nos dois sentidos: novoInicio < existenteFim  E  existenteInicio < novoFim
        return agendamentosDoProfissional.stream().anyMatch(existente ->
                novoInicio.isBefore(existente.getFim()) && existente.getInicio().isBefore(novoFim)
        );
    }

    private Cliente buscarClienteOuFalhar(Long id) {
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private Profissional buscarProfissionalOuFalhar(Long id) {
        return profissionalRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado"));
    }

    private Servico buscarServicoOuFalhar(Long id) {
        return servicoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
    }
}
