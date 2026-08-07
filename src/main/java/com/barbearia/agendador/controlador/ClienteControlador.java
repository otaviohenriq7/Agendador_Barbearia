package com.barbearia.agendador.controlador;

import com.barbearia.agendador.modelo.Cliente;
import com.barbearia.agendador.repositorio.ClienteRepositorio;
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

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return clienteRepositorio.findById(id)
                .map(existente -> {
                    existente.setNome(cliente.getNome());
                    existente.setTelefone(cliente.getTelefone());
                    existente.setEmail(cliente.getEmail());
                    return ResponseEntity.ok(clienteRepositorio.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
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
