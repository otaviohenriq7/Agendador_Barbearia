package com.otavio.agendador.controlador;

import com.otavio.agendador.modelo.Profissional;
import com.otavio.agendador.repositorio.ProfissionalRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalControlador {

    private final ProfissionalRepositorio profissionalRepositorio;

    public ProfissionalControlador(ProfissionalRepositorio profissionalRepositorio) {
        this.profissionalRepositorio = profissionalRepositorio;
    }

    @GetMapping
    public List<Profissional> listar() {
        return profissionalRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(@PathVariable Long id) {
        return profissionalRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profissional criar(@Valid @RequestBody Profissional profissional) {
        return profissionalRepositorio.save(profissional);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profissional> atualizar(@PathVariable Long id, @Valid @RequestBody Profissional profissional) {
        return profissionalRepositorio.findById(id)
                .map(existente -> {
                    existente.setNome(profissional.getNome());
                    existente.setTelefone(profissional.getTelefone());
                    return ResponseEntity.ok(profissionalRepositorio.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!profissionalRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        profissionalRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
