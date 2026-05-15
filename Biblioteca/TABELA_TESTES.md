Tabela de Testes

Todos os testes abaixo são executados automaticamente pela classe br.edu.biblioteca.Main.

| # | Teste | Ação realizada | Resultado esperado | Resultado obtido |
|---|-------|---------------|-------------------|-----------------|
| 1 | Cadastrar livro Java | `c.cadastrarLivro(...)` | Livro salvo no banco com id gerado | ✔ PASSOU |
| 2 | Cadastrar livro Banco de Dados | `c.cadastrarLivro(...)` | Livro salvo no banco com id gerado | ✔ PASSOU |
| 3 | Cadastrar livro Projetor | `c.cadastrarLivro(...)` | Livro salvo no banco com id gerado | ✔ PASSOU |
| 4 | Cadastrar usuário Alexandre | `c.cadastrarUsuario(...)` | Usuário salvo no banco com id gerado | ✔ PASSOU |
| 5 | Cadastrar usuário José | `c.cadastrarUsuario(...)` | Usuário salvo no banco com id gerado | ✔ PASSOU |
| 6 | Matrícula duplicada | `c.cadastrarUsuario(...)` com matrícula repetida | Retorna o id já existente (sem duplicar) | ✔ PASSOU |
| 7 | Empréstimo válido — Java para Alexandre | `c.emprestar(java, alex)` | Empréstimo registrado; `true` retornado | ✔ PASSOU |
| 8 | Empréstimo válido — Projetor para José | `c.emprestar(projetor, jose)` | Empréstimo registrado; `true` retornado | ✔ PASSOU |
| 9 | Livro Java ficou indisponível | `c.livroEstaIndisponivel(java)` | `disponivel = 0` no banco | ✔ PASSOU |
| 10 | Livro Projetor ficou indisponível | `c.livroEstaIndisponivel(projetor)` | `disponivel = 0` no banco | ✔ PASSOU |
| 11 | Bloquear livro já emprestado | `c.emprestar(java, jose)` | `false` retornado; sem novo registro no banco | ✔ PASSOU |
| 12 | Empréstimo indevido não salvo | `c.emprestimoExisteNoBanco(java, jose)` | `false` — registro não existe | ✔ PASSOU |
| 13 | Bloquear livro inexistente | `c.emprestar(9999, alex)` | `false` retornado | ✔ PASSOU |
| 14 | Empréstimo indevido não salvo | `c.emprestimoExisteNoBanco(9999, alex)` | `false` — registro não existe | ✔ PASSOU |
| 15 | Devolução válida | `c.devolver(java, alex)` | Status → FINALIZADO; livro liberado | ✔ PASSOU |
| 16 | Livro Java voltou a ficar disponível | `c.livroEstaIndisponivel(java)` | `disponivel = 1` no banco | ✔ PASSOU |
| 17 | Bloquear segunda devolução | `c.devolver(java, alex)` | `false` — sem empréstimo ativo | ✔ PASSOU |

Observações

- Os testes 6, 11, 12, 13, 14 e 17 validam casos negativos (operações que devem ser bloqueadas).
- Todos os testes verificam o estado real do banco (não apenas o retorno do método).
- A saída completa do console está registrada em EVIDENCIA_EXECUCAO.txt.