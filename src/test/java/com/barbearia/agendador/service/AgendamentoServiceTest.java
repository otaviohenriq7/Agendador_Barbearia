package com.barbearia.agendador.service;

import com.barbearia.agendador.modelo.Agendamento;
import com.barbearia.agendador.modelo.Cliente;
import com.barbearia.agendador.modelo.Profissional;
import com.barbearia.agendador.modelo.Servico;
import com.barbearia.agendador.repositorio.ClienteRepositorio;
import com.barbearia.agendador.repositorio.ProfissionalRepositorio;
import com.barbearia.agendador.repositorio.ServicoRepositorio;
import com.barbearia.agendador.requisicao.NovoAgendamentoRequisicao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AgendamentoServiceTest {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private ProfissionalRepositorio profissionalRepositorio;

    @Autowired
    private ServicoRepositorio servicoRepositorio;

    private static final LocalDateTime DEZ_HORAS = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

    private Cliente cliente;
    private Profissional carlos;
    private Profissional ana;
    private Servico corteDe30Minutos;

    @BeforeEach
    void prepararDados() {
        cliente = clienteRepositorio.save(new Cliente("Joao", "11999998888", "joao@email.com"));
        carlos = profissionalRepositorio.save(new Profissional("Carlos", "11977776666"));
        ana = profissionalRepositorio.save(new Profissional("Ana", "11955554444"));
        corteDe30Minutos = servicoRepositorio.save(new Servico("Corte", 30));
    }

    @Test
    @DisplayName("cria o agendamento quando o horario esta livre")
    void criaQuandoHorarioLivre() {
        Agendamento agendamento = agendar(carlos, DEZ_HORAS);

        assertNotNull(agendamento.getId());
        assertEquals(DEZ_HORAS, agendamento.getInicio());
        assertEquals(DEZ_HORAS.plusMinutes(30), agendamento.getFim());
    }

    @Test
    @DisplayName("recusa agendamento que sobrepoe outro do mesmo profissional")
    void recusaQuandoSobrepoe() {
        agendar(carlos, DEZ_HORAS);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> agendar(carlos, DEZ_HORAS.plusMinutes(15)));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    @DisplayName("aceita agendamento que comeca no minuto em que o anterior termina")
    void aceitaQuandoEncostaNoAnterior() {
        agendar(carlos, DEZ_HORAS);

        Agendamento seguinte = agendar(carlos, DEZ_HORAS.plusMinutes(30));

        assertNotNull(seguinte.getId());
    }

    @Test
    @DisplayName("aceita o mesmo horario para um profissional diferente")
    void aceitaMesmoHorarioComOutroProfissional() {
        agendar(carlos, DEZ_HORAS);

        Agendamento daAna = agendar(ana, DEZ_HORAS.plusMinutes(15));

        assertNotNull(daAna.getId());
    }

    @Test
    @DisplayName("aceita atualizacao que nao muda o horario, sem conflitar consigo mesmo")
    void aceitaAtualizacaoSemMudarHorario() {
        Agendamento agendamento = agendar(carlos, DEZ_HORAS);

        Agendamento atualizado = agendamentoService.atualizar(agendamento.getId(),
                requisicao(carlos, DEZ_HORAS));

        assertEquals(agendamento.getId(), atualizado.getId());
        assertEquals(DEZ_HORAS, atualizado.getInicio());
    }

    @Test
    @DisplayName("recusa atualizacao que invade outro agendamento")
    void recusaAtualizacaoQueInvadeOutro() {
        Agendamento primeiro = agendar(carlos, DEZ_HORAS);
        agendar(carlos, DEZ_HORAS.plusHours(1));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> agendamentoService.atualizar(primeiro.getId(),
                        requisicao(carlos, DEZ_HORAS.plusMinutes(75))));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    @DisplayName("recusa agendamento em horario que ja passou")
    void recusaHorarioNoPassado() {
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> agendar(carlos, LocalDateTime.now().minusDays(1)));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }

    @Test
    @DisplayName("falha quando o profissional informado nao existe")
    void falhaQuandoProfissionalNaoExiste() {
        NovoAgendamentoRequisicao requisicao = new NovoAgendamentoRequisicao(
                cliente.getId(), 9999L, corteDe30Minutos.getId(), DEZ_HORAS);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> agendamentoService.agendar(requisicao));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    private Agendamento agendar(Profissional profissional, LocalDateTime inicio) {
        return agendamentoService.agendar(requisicao(profissional, inicio));
    }

    private NovoAgendamentoRequisicao requisicao(Profissional profissional, LocalDateTime inicio) {
        return new NovoAgendamentoRequisicao(
                cliente.getId(), profissional.getId(), corteDe30Minutos.getId(), inicio);
    }
}
