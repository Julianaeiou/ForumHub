# ForumHub

Projeto Spring Boot simples para gerenciar tópicos de um fórum (CRUD). O projeto inclui migrações Flyway para criar a tabela de tópicos e um índice único case-insensitive para evitar duplicatas.

---

## Sumário
- Descrição
- Requisitos
- Como executar
- Migrações
- Endpoints (POST /topicos, GET /topicos, GET /topicos/{id}, PUT /topicos/{id}, DELETE /topicos/{id})
- Autenticação
- Exemplos de uso 
- Validações e regras de negócio

---

## Descrição
ForumHub é uma API REST construída com Spring Boot que permite criar, listar, consultar, atualizar e excluir tópicos de um fórum. Os dados são persistidos em PostgreSQL usando Spring Data JPA. As migrações de banco são gerenciadas por Flyway.

## Requisitos
- Java 21+ (o `pom.xml` define `<java.version>21</java.version>`)
- Maven
- PostgreSQL (ou outra fonte de dados compatível configurada em `application.properties`)

## Configuração do banco
Por padrão o projeto usa as configurações em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Forum
spring.datasource.username=postgres
spring.datasource.password=123456
spring.flyway.baseline-on-migrate=true
```

Ajuste `spring.datasource.*` conforme seu ambiente local.

As migrações estão em `src/main/resources/db/migration`:
- `V2__create_topicos_table.sql` — cria tabela `topicos`.
- `V3__create_ci_unique_index_on_topicos.sql` — cria índice UNIQUE case-insensitive sobre lower(titulo), lower(mensagem).

Flyway aplicará automaticamente essas migrações ao iniciar a aplicação.

## Como executar
1. Configure o banco (PostgreSQL) e ajuste `application.properties` se necessário.
2. Compilar e rodar:

```bash
mvn clean package
mvn spring-boot:run
```

ou apenas:

```bash
mvn spring-boot:run
```

O projeto roda por padrão em `http://localhost:8080`.

## Autenticação
O projeto usa Spring Security. Para desenvolvimento criei credenciais em `application.properties`:

```properties
spring.security.user.name=admin
spring.security.user.password=secret123
spring.security.user.roles=ADMIN
```

- Usuário padrão: `admin`
- Senha padrão: `secret123`

Observação: se você não encontrar essas propriedades, o Spring pode gerar um password aleatório e imprimi-lo nos logs ao iniciar a aplicação — verá uma linha parecida com:
```
Using generated security password: <senha-gerada>
```
Use esse par `user:<senha-gerada>` ou configure `spring.security.user.*` explicitamente.

> Nota sobre CSRF: o projeto atual exige autenticação Basic; se CSRF estiver habilitado (configuração padrão do Spring Security), chamadas POST/PUT/DELETE feitas via clientes sem token CSRF podem ser bloqueadas. Para testes via curl usando Basic auth, as chamadas normalmente funcionam; caso observe erros 401/403, verifique logs e a configuração de security.

## Endpoints
Todos os endpoints abaixo estão sob a base `/topicos`.

1. POST /topicos — cria um novo tópico
- Corpo (JSON):
  - titulo (String) — obrigatório
  - mensagem (String) — obrigatório
  - autor (String) — obrigatório
  - curso (String) — obrigatório
- Validação: usa `@Valid`/`@NotBlank`/`@Size`.
- Regras: não permite duplicatas (mesmo título + mensagem, case-insensitive).
- Respostas:
  - 201 Created + Header `Location: /topicos/{id}` quando criado
  - 400 Bad Request em validação
  - 409 Conflict em duplicata

2. GET /topicos — lista todos os tópicos
- Retorna 200 OK com um array JSON de tópicos contendo: id, titulo, mensagem, dataCriacao, status, autor, curso.

3. GET /topicos/{id} — detalha um tópico
- Parâmetro: `id` via `@PathVariable` (Long)
- Retorna 200 OK com o tópico (TopicoResponse) ou 404 Not Found se não existir.

4. PUT /topicos/{id} — atualiza um tópico
- Parâmetro: `id` via `@PathVariable` (Long)
- Corpo: mesmo formato do POST; validador `@Valid` também aplicado.
- Regras: validar campos obrigatórios; verificar duplicata case-insensitive (ignorar o próprio id) — se duplicar com outro registro retorna 409.
- Respostas:
  - 200 OK com o recurso atualizado
  - 404 Not Found se o id não existir
  - 400 Bad Request em erro de validação
  - 409 Conflict em duplicata

5. DELETE /topicos/{id} — exclui um tópico
- Parâmetro: `id` via `@PathVariable` (Long)
- Ação: verifica existência com `findById()`; se existir chama `deleteById(id)`.
- Respostas:
  - 204 No Content quando excluído
  - 404 Not Found se não existir

## Formato das entidades (resumo)
- Topico:
  - id: Long
  - titulo: String
  - mensagem: String
  - autor: String
  - curso: String
  - status: Enum (NAO_RESPONDIDO, RESPONDIDO, FECHADO)
  - dataCriacao: LocalDateTime

## Exemplos de requisição
Substitua credenciais quando necessário.

Exemplo POST (curl):
```bash
curl -u admin:secret123 -H "Content-Type: application/json" \
  -d '{"titulo":"Dúvida sobre JPA","mensagem":"Como funciona o mapeamento OneToMany?","autor":"Daniel","curso":"Spring Boot"}' \
  http://localhost:8080/topicos
```

Exemplo GET (listar):
```bash
curl -u admin:secret123 http://localhost:8080/topicos
```

Exemplo PUT:
```bash
curl -u admin:secret123 -H "Content-Type: application/json" -X PUT -d '{"titulo":"Novo titulo","mensagem":"nova msg","autor":"Daniel","curso":"Spring Boot"}' http://localhost:8080/topicos/1
```

Exemplo DELETE:
```bash
curl -u admin:secret123 -X DELETE http://localhost:8080/topicos/1
```

## Validações e regras de negócio (resumo)
- Todos os campos do DTO de criação/atualização são obrigatórios (@NotBlank).
- Não permitir criação/atualização de tópicos duplicados (mesmo titulo + mensagem). A verificação é case-insensitive e há um índice único no banco para garantir a integridade.
- Normalização: trims nas extremidades são aplicados nos DTOs antes de salvar.
