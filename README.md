# Império Fitness

Aplicação Spring Boot 4, Thymeleaf, HTML/CSS e PostgreSQL.

## Rotas

- `/`: Home da academia
- `/cadastro`: cadastro e login do aluno
- `/ativar-conta?token=...`: ativação de conta criada na recepção
- `/portal`: Portal do Aluno (requer autenticação)
- `/admin/home`: Admin da Home V4 (requer papel ADMIN)
- `/admin/portal`: Admin do Portal do Aluno (requer papel ADMIN)

## Primeiro administrador

O sistema **não** cria um administrador com senha padrão. Para criar uma conta administrativa inicial, defina as variáveis de ambiente `ADMIN_EMAIL` e `ADMIN_PASSWORD` (senha com pelo menos 12 caracteres) antes de iniciar o backend. O sistema cria a conta apenas se o e-mail não estiver cadastrado. Ele **não** promove contas de clientes existentes automaticamente. Depois, acesse `/cadastro?modo=login` para entrar.

Não registre credenciais no Git, no README ou no `application.properties`.

## Admin do Portal

O painel tem cinco áreas: Visão Geral, Cadastrar Aluno, Gestão de Treinos, Planos e Mensalidades e Notificações.

- Alunos: listagem e edição de dados e situação do cadastro facial.
- Recepção: cria aluno e entrega link de ativação válido por 48 horas. O próprio aluno define a senha.
- Treinos: cria, edita, renomeia e exclui modelos; atribui ficha a um aluno.
- Planos: altera plano e status, registra pagamentos manuais e vencimentos.
- Notificações: envia avisos para todos, alunos ativos ou um aluno específico. O aluno controla o recebimento com um único botão no Perfil.

As informações acima são persistidas pelo JPA no PostgreSQL. O Admin da Home V4 foi preservado como módulo separado e ainda possui partes baseadas em armazenamento local. O Portal do Aluno mantém funcionalidades demonstrativas herdadas do protótipo, como evolução e frequência, que **não** são dados oficiais do backend.

## Execução

Configure `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` conforme o seu PostgreSQL e execute, dentro de `backend`:

```powershell
.\mvnw.cmd spring-boot:run
```

As alterações foram feitas no repositório. Antes de atualizar sua cópia local, verifique `git status` para não sobrescrever alterações não commitadas. Execute testes de cadastro, ativação, login, matrícula, pagamento, treinos e notificações no ambiente real antes de colocar em produção.
