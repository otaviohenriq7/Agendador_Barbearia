# Sistema de Agendas (Barbearia)

API REST + interface web para agendamento de barbearia. Java 21, Spring Boot 4 e PostgreSQL.

Fiz esse projeto pra praticar regra de negócio de verdade, não só CRUD. O problema central não é
guardar cadastro, é impedir que o mesmo profissional seja marcado em dois horários que se
sobrepõem. Cliente, profissional e serviço existem pra dar suporte a isso.

## Como rodar

Precisa de JDK 21, Docker e Git.

```bash
git clone https://github.com/otaviohenriq7/Agendador_Barbearia.git
```

```bash
cd Agendador_Barbearia
```

Sobe o banco:

```bash
docker compose up -d
```

Sobe a aplicação (no Windows é `mvnw.cmd`):

```bash
./mvnw spring-boot:run
```

Acessa http://localhost:8080 e entra com `admin` / `admin123`. Em produção esses valores vêm das
variáveis `ADMIN_LOGIN`, `ADMIN_SENHA` e `JWT_SECRET`.

Pra parar o banco sem perder dados, `docker compose stop`. Com `down -v` ele apaga o volume junto.

Testes:

```bash
./mvnw test
```

Rodam em H2, então não precisam do Docker ligado.

## API

Tudo exige token, menos o `POST /login` e os arquivos da interface. Sem token válido, `401`.

O login devolve `{ "token": "..." }` e o token vai nas outras chamadas em
`Authorization: Bearer <token>`. Vale 8 horas.

Os quatro recursos (`/clientes`, `/profissionais`, `/servicos`, `/agendamentos`) têm o mesmo CRUD:

| Método | Rota | Retorno |
|---|---|---|
| `GET` | `/clientes` | Lista todos |
| `GET` | `/clientes/{id}` | Um registro, ou `404` |
| `POST` | `/clientes` | `201`, ou `400` se inválido |
| `PUT` | `/clientes/{id}` | `200`, `400` ou `404` |
| `DELETE` | `/clientes/{id}` | `204`, ou `404` |

Agendamento é o único diferente: recebe só os IDs, e não os objetos inteiros.

```json
{
  "clienteId": 1,
  "profissionalId": 1,
  "servicoId": 1,
  "inicio": "2026-08-10T10:00:00"
}
```

## Decisões

**Só o agendamento tem service.** Criar agendamento precisa buscar três entidades e validar
conflito. Listar cliente não precisa de nada disso, então o controller chama o repositório direto.
Não vi motivo pra criar service vazio nos outros.

**O fim do agendamento não fica no banco.** Calculo com o início mais a duração do serviço. Se
guardasse os dois, uma hora alguém mudaria um e esqueceria o outro. O efeito colateral é que mudar
a duração de um serviço muda o fim de agendamentos antigos, e isso eu ainda não tratei.

**Regras de agendamento ficam todas no service.** Conflito de horário e proibição de agendar no
passado estão os dois no `AgendamentoService`. A do passado só vale na criação, porque corrigir um
agendamento antigo é uma coisa legítima de fazer.

**JWT em vez de sessão.** A API não guarda estado, valida a assinatura do token a cada requisição.
Por isso o CSRF fica desligado, já que não existe cookie de sessão aqui. Sei que o custo é o token
ficar acessível ao JavaScript.

**H2 só nos testes.** O banco de desenvolvimento é PostgreSQL em container, mas os testes usam H2
em memória. Não queria um build que só passa se o Docker estiver ligado.

## Estrutura

```
src/main/java/com/barbearia/agendador/
├── modelo/         entidades JPA (Cliente, Profissional, Servico, Agendamento, Usuario)
├── repositorio/    acesso ao banco via Spring Data JPA
├── service/        regra de negócio (apenas AgendamentoService)
├── seguranca/      configuração do Spring Security, geração e leitura do token
├── requisicao/     objetos de entrada (agendamento e login)
├── resposta/       objeto de saída do login
└── controlador/    endpoints REST

src/main/resources/static/
├── index.html
├── css/style.css
└── js/
    ├── api.js      chamadas à API
    └── app.js      roteamento e telas
```

O front é servido pelo próprio Spring, então interface e API vêm da mesma origem. O roteamento é
pelo `#` do endereço (`#/clientes`), sem framework.

### Arquivos de build

O `pom.xml` é a configuração do Maven. É lá que declaro as dependências (Spring Boot, driver do
PostgreSQL, H2 de teste), a versão do Java e os plugins. Não baixei nenhum `.jar` na mão: o Maven
lê esse arquivo, busca as bibliotecas e monta o classpath.

O `mvnw` e o `mvnw.cmd` são o Maven Wrapper, sendo o `.cmd` o do Windows e o outro do Linux e Mac.
São scripts que baixam sozinhos a versão certa do Maven, definida em
`.mvn/wrapper/maven-wrapper.properties`. Serve pra quem clonar o repositório conseguir rodar sem
ter Maven instalado, e na mesma versão que eu usei.

## Tecnologias

- Java 21
- Spring Boot 4.1.0 — Web, Data JPA, Validation, Security
- JWT (jjwt) para autenticação
- PostgreSQL 17 em container (Docker Compose)
- H2 (testes)
- Maven Wrapper
- HTML, CSS e JavaScript sem framework
