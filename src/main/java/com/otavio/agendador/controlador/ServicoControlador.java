package com.otavio.agendador.controlador;

import com.otavio.agendador.modelo.Servico;
import com.otavio.agendador.repositorio.ServicoRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoControlador {

    private final ServicoRepositorio servicoRepositorio;

    public ServicoControlador(ServicoRepositorio servicoRepositorio) {
        this.servicoRepositorio = servicoRepositorio;
    }

    @GetMapping
    public List<Servico> listar() {
        return servicoRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return servicoRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Servico criar(@Valid @RequestBody Servico servico) {
        return servicoRepositorio.save(servico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(@PathVariable Long id, @Valid @RequestBody Servico servico) {
        return servicoRepositorio.findById(id)
                .map(existente -> {
                    existente.setNome(servico.getNome());
                    existente.setDuracaoEmMinutos(servico.getDuracaoEmMinutos());
                    return ResponseEntity.ok(servicoRepositorio.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!servicoRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        servicoRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
