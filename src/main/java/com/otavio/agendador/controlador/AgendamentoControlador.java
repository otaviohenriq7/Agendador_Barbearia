package com.otavio.agendador.controlador;

import com.otavio.agendador.modelo.Agendamento;
import com.otavio.agendador.repositorio.AgendamentoRepositorio;
import com.otavio.agendador.requisicao.NovoAgendamentoRequisicao;
import com.otavio.agendador.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoControlador {

    private final AgendamentoRepositorio agendamentoRepositorio;
    private final AgendamentoService agendamentoService;

    public AgendamentoControlador(AgendamentoRepositorio agendamentoRepositorio,
                                   AgendamentoService agendamentoService) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public List<Agendamento> listar() {
        return agendamentoRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        return agendamentoRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agendamento criar(@Valid @RequestBody NovoAgendamentoRequisicao requisicao) {
        return agendamentoService.agendar(requisicao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!agendamentoRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        agendamentoRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
