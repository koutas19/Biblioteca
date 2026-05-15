 Sistema de Controle de Empréstimo de Livros

 Integrantes

Alexandre de Souza Mendes

Descrição do problema

O sistema gerencia o empréstimo de livros de uma biblioteca acadêmica. Permite cadastrar usuários (alunos) e livros, realizar empréstimos, registrar devoluções e consultar o histórico. As regras de negócio garantem que um livro indisponível ou inexistente não possa ser emprestado, e que um usuário inexistente não possa realizar empréstimos.

Regras de negócio:

- Um livro só pode ser emprestado se estiver disponível (`disponivel = 1`).
- Um empréstimo só é registrado se o usuário e o livro existirem no banco.
- A matrícula do usuário é única — duplicatas retornam o id já existente.
- A devolução localiza o empréstimo mais recente com status `ATIVO` e o finaliza.
- Empréstimo e atualização de disponibilidade ocorrem na mesma transação (commit/rollback).

 Requisitos implementados

- [x] Cadastrar usuário no banco
- [x] Cadastrar livro no banco
- [x] Listar todos os livros
- [x] Listar livros disponíveis (filtro `disponivel = 1`)
- [x] Realizar empréstimo válido
- [x] Bloquear empréstimo de livro indisponível
- [x] Bloquear empréstimo de livro inexistente
- [x] Bloquear empréstimo de usuário inexistente
- [x] Bloquear matrícula duplicada
- [x] Registrar devolução (atualiza status e libera o livro)
- [x] Bloquear devolução sem empréstimo ativo
- [x] Consultar empréstimos ativos
- [x] Consultar histórico completo de empréstimos

 Diagramas UML

Os diagramas estão na pasta diagramas/:

| Arquivo | Descrição |
|---------|-----------|
| `diagramas/casos_de_uso.png` | Diagrama de casos de uso |
| `diagramas/diagrama_classes.png` | Diagrama de classes |
| `diagramas/diagrama_sequencia.png` | Diagrama de sequência — realizar empréstimo |
| `diagramas/modelo_er.png` | Modelo entidade-relacionamento |

 Diagrama de casos de uso

 Atores: Usuário/Aluno · Bibliotecário/Sistema

 Casos de uso principais:

- Cadastrar usuário
- Cadastrar livro
- Listar livros disponíveis
- Realizar empréstimo
- Registrar devolução
- Consultar empréstimos ativos
- Consultar histórico de empréstimos

 Diagrama de classes

Pacote `model`: `Usuario`, `Livro`, `Emprestimo`

Pacote `dao`: `UsuarioDAO`, `LivroDAO`, `EmprestimoDAO`

Pacote `database`: `ConexaoSQLite`

Pacote `controller`: `BibliotecaController`

Relacionamentos:
- `Usuario` 1 ——< `Emprestimo` (um usuário pode ter vários empréstimos)
- `Livro` 1 ——< `Emprestimo` (um livro pode ter vários empréstimos ao longo do tempo)
- `BibliotecaController` usa `UsuarioDAO`, `LivroDAO` e `EmprestimoDAO`
- `UsuarioDAO`, `LivroDAO` e `EmprestimoDAO` usam `ConexaoSQLite`

 Diagrama de sequência — Realizar empréstimo

Main → BibliotecaController.emprestar(idLivro, idUsuario)
         → UsuarioDAO.existePorId(idUsuario)        [verifica usuário]
         → LivroDAO.existePorId(idLivro)            [verifica livro]
         → LivroDAO.disponivel(idLivro)             [verifica disponibilidade]
         → EmprestimoDAO.emprestar(idLivro, idUsuario)
              → INSERT INTO emprestimo              [registra empréstimo]
              → UPDATE livro SET disponivel = 0     [marca livro como indisponível]
              → conn.commit()
         ← true (sucesso) ou false (bloqueado)

 Modelo do banco de dados

 Diagrama ER

┌─────────────────────────┐        ┌───────────────────────────────────┐
│         usuario         │        │              livro                │
├─────────────────────────┤        ├───────────────────────────────────┤
│ id        INTEGER PK AI │        │ id         INTEGER PK AI          │
│ nome      TEXT NOT NULL │        │ titulo     TEXT NOT NULL          │
│ matricula TEXT UNIQUE   │        │ autor      TEXT                   │
│ curso     TEXT NOT NULL │        │ tipo       TEXT                   │
└───────────┬─────────────┘        │ disponivel INTEGER DEFAULT 1      │
            │                      └──────────────┬────────────────────┘
            │                                     │
            │  ┌──────────────────────────────────┴────────────────────┐
            └──┤                    emprestimo                         │
               ├───────────────────────────────────────────────────────┤
               │ id              INTEGER PK AI                         │
               │ id_usuario      INTEGER FK → usuario(id)              │
               │ id_livro        INTEGER FK → livro(id)                │
               │ data_emprestimo TEXT NOT NULL                         │
               │ data_devolucao  TEXT                                  │
               │ status          TEXT DEFAULT 'ATIVO'                  │
               └───────────────────────────────────────────────────────┘

 Tabelas

usuario

| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| nome | TEXT | NOT NULL |
| matricula | TEXT | NOT NULL, UNIQUE |
| curso | TEXT | NOT NULL |

livro

| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| titulo | TEXT | NOT NULL |
| autor | TEXT | — |
| tipo | TEXT | — |
| disponivel | INTEGER | DEFAULT 1 |

emprestimo

| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | INTEGER | PK, AUTOINCREMENT |
| id_usuario | INTEGER | NOT NULL, FK → usuario(id) |
| id_livro | INTEGER | NOT NULL, FK → livro(id) |
| data_emprestimo | TEXT | NOT NULL |
| data_devolucao | TEXT | — |
| status | TEXT | DEFAULT 'ATIVO' |

 como criar o banco SQLite

O banco é criado automaticamente ao executar o projeto. Para criar manualmente:

```bash
sqlite3 biblioteca.db < script.sql
```
O arquivo `script.sql` recria todas as tabelas e insere dados de exemplo.

 Verificando o banco SQLite

Abra o banco:

```bash
sqlite3 biblioteca.db
```

Verifique as tabelas:

```sql
.tables
```
 Deve aparecer:

usuario
livro
emprestimo

 Confere os dados:

```sql
SELECT * FROM usuario;
SELECT * FROM livro;
SELECT * FROM emprestimo;
```
Saída esperada:

sqlite> SELECT * FROM usuario;
╭────┬──────────────────┬───────────┬────────────╮
│ id │       nome       │ matricula │   curso    │
╞════╪══════════════════╪═══════════╪════════════╡
│  1 │ Alexandre Mendes │ '2024001' │ ADS        │
│  2 │ José Silva       │ '2024002' │ Computação │
╰────┴──────────────────┴───────────┴────────────╯

sqlite> SELECT * FROM livro;
╭────┬──────────────────────┬─────────────────┬────────────────┬────────────╮
│ id │        titulo        │      autor      │      tipo      │ disponivel │
╞════╪══════════════════════╪═════════════════╪════════════════╪════════════╡
│  1 │ Java: Como Programar │ Deitel & Deitel │ Programação    │          1 │
│  2 │ Banco de Dados       │ Ramez Elmasri   │ Banco de Dados │          1 │
│  3 │ Projetor Epson       │ Laboratório     │ Multimídia     │          1 │
│  4 │ Algoritmos           │ Cormen          │ Programação    │          1 │
╰────┴──────────────────────┴─────────────────┴────────────────┴────────────╯

sqlite> SELECT * FROM emprestimo;
sqlite>
```
Para sair do SQLite, digite `.quit`.

 como executar o projeto Java

Pré-requisitos: Java 11+, Maven 3.6+

 Compilar
mvn compile

 Executar
mvn exec:java -Dexec.mainClass="br.edu.biblioteca.Main"
```

Ou abrir o projeto no IntelliJ IDEA e executar a classe `br.edu.biblioteca.Main` diretamente.

O sistema cria o banco, executa todos os testes e exibe o estado final no console.

Testes realizados:

Os testes estão documentados em `TABELA_TESTES.md` e são executados automaticamente ao rodar o `Main`.

| # | Cenário | Resultado esperado |
|---|---------|-------------------|
| 1 | Cadastrar livro | Livro salvo; id gerado retornado |
| 2 | Cadastrar usuário | Usuário salvo; id gerado retornado |
| 3 | Matrícula duplicada | Retorna id já existente, sem erro |
| 4 | Empréstimo válido | Empréstimo registrado; livro marcado como indisponível |
| 5 | Livro já emprestado | Operação bloqueada; nenhum registro criado |
| 6 | Livro inexistente | Operação bloqueada; nenhum registro criado |
| 7 | Devolução válida | Status atualizado para FINALIZADO; livro liberado |
| 8 | Segunda devolução (sem ativo) | Operação bloqueada |

Decisões de projeto

| Decisão | Justificativa |
|---------|---------------|
| SQLite como banco | Sem necessidade de servidor; arquivo único; ideal para entrega acadêmica |
| Padrão DAO | Separa a lógica de persistência das regras de negócio; facilita manutenção |
| Controller (GRASP) | `BibliotecaController` centraliza operações; `Main` não acessa DAOs diretamente |
| Information Expert (GRASP) | Cada DAO é responsável pelas consultas da sua própria entidade |
| Transações com commit/rollback | Empréstimo e atualização de disponibilidade são atômicos |
| Banco reinicializado a cada execução | Garante estado limpo para demonstração dos testes |
| Foreign Keys ativas (`PRAGMA foreign_keys = ON`) | Garante integridade referencial no SQLite |

 Melhorias futuras

- Interface gráfica (Swing ou JavaFX)
- Pesquisa de livros por título, autor ou tipo
- Limite de empréstimos simultâneos por usuário
- Controle de prazo de devolução e multas por atraso
- Relatórios exportáveis (PDF, CSV)
- Login com perfil de administrador e usuário comum
- Migração para banco de dados servidor (PostgreSQL ou MySQL) em produção