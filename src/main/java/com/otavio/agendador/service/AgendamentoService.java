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

        if (existeConflito(novoAgendamento, null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esse profissional já tem um agendamento nesse horário");
        }

        return agendamentoRepositorio.save(novoAgendamento);
    }

    public Agendamento atualizar(Long id, NovoAgendamentoRequisicao requisicao) {
        Agendamento agendamento = agendamentoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        Cliente cliente = buscarClienteOuFalhar(requisicao.clienteId());
        Profissional profissional = buscarProfissionalOuFalhar(requisicao.profissionalId());
        Servico servico = buscarServicoOuFalhar(requisicao.servicoId());

        Agendamento candidato = new Agendamento(cliente, profissional, servico, requisicao.inicio());

        if (existeConflito(candidato, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esse profissional já tem um agendamento nesse horário");
        }

        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setInicio(requisicao.inicio());

        return agendamentoRepositorio.save(agendamento);
    }

    private boolean existeConflito(Agendamento candidato, Long idParaIgnorar) {
        LocalDateTime inicio = candidato.getInicio();
        LocalDateTime fim = candidato.getFim();

        List<Agendamento> agendamentosDoProfissional =
                agendamentoRepositorio.findByProfissionalId(candidato.getProfissional().getId());

        return agendamentosDoProfissional.stream()
                .filter(outro -> !outro.getId().equals(idParaIgnorar))
                .anyMatch(outro -> inicio.isBefore(outro.getFim()) && outro.getInicio().isBefore(fim));
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
