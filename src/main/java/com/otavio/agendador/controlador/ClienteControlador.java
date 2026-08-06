package com.otavio.agendador.controlador;

import com.otavio.agendador.modelo.Cliente;
import com.otavio.agendador.repositorio.ClienteRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteControlador {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteControlador(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepositorio.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteRepositorio.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criar(@Valid @RequestBody Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!clienteRepositorio.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteRepositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
