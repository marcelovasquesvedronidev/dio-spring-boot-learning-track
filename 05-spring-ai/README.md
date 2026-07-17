# Budgeting API - Spring Boot + Spring AI (Desafio DIO)

Fork: https://github.com/marcelovasquesvedronidev/dio-spring-boot-learning-track/tree/main/05-spring-ai

## O que o projeto faz

É uma API de controle financeiro (orçamento pessoal) construída com Spring Boot 4 e Spring AI. Ela permite cadastrar usuários, autenticar via JWT, registrar transações financeiras e consultá-las por categoria ou por período (data inicial e final), além de processar comandos de voz: o usuário envia um áudio, a API transcreve o conteúdo, identifica a intenção (criar ou consultar uma transação), executa a ação correspondente e devolve a resposta final também em áudio (texto → fala).

O projeto segue arquitetura em camadas (Clean/Hexagonal Architecture), separando:

- **domain**: entidades e regras de negócio (`Transaction`, `Category`, `User`, `TransactionRepository`)
- **application**: casos de uso (`PersistTransactionUseCase`, `ListTransactionsByCategoryUseCase`, `ListTransactionsByPeriodUseCase`), usados tanto pela API REST quanto pelas ferramentas (`@Tool`) que o modelo de IA pode acionar
- **infrastructure**: adaptadores HTTP (controllers, requests), persistência JPA (entities, repositories) e segurança (JWT, filtros, configurações)

## Tecnologias utilizadas

- Java + Spring Boot 4
- Spring AI (`spring.ai`) para transcrição de áudio (Speech-to-Text), tool calling e conversão de texto em áudio (Text-to-Speech), integrando com a API da OpenAI
- Spring Security + JWT para autenticação stateless
- Spring Data JPA
- MySQL (via Docker Compose)
- Gradle

## Como executar a aplicação

1. Suba o banco de dados MySQL com Docker Compose:
   ```bash
   docker compose up -d
   ```

2. Configure as variáveis de ambiente necessárias:
   ```bash
   export OPENAI_API_KEY="sua_chave_aqui"
   export JWT_SECRET="seu_segredo_aqui"
   ```

3. Rode a aplicação:
   ```bash
   ./gradlew bootRun
   ```

## Qual melhoria eu implementei

Implementei duas melhorias em cima do projeto base:

### 1. Autenticação JWT stateless

- `AuthenticationController` (endpoint `/login`) para autenticar o usuário e gerar o token
- `UserController` (endpoint `/users`) para cadastro de usuário
- `JwtService`, responsável por gerar e validar os tokens
- `AutenticacaoService`, responsável pela lógica de autenticação
- `SecurityFilter` e `SecurityConfigurations`, para interceptar as requisições, validar o token JWT no header `Authorization` e proteger os endpoints de transações (`/transactions`) e do fluxo de IA (`/ai`)

Isso garante que apenas usuários autenticados consigam criar/consultar transações ou usar o endpoint de comandos de voz, sem que a API precise manter sessão em memória (autenticação stateless).

### 2. Consulta de transações por período

Adicionei a possibilidade de filtrar transações entre duas datas, sem alterar nenhum comportamento já existente:

- Campo `date` adicionado em `Transaction` (domain) e `TransactionEntity` (persistência), preenchido automaticamente na criação da transação
- Novo método `findByPeriod` no `TransactionRepository`, implementado via query derivada (`findAllByDateBetween`) no Spring Data JPA
- `ListTransactionsByPeriodUseCase`, novo caso de uso que segue o mesmo padrão do `ListTransactionsByCategoryUseCase` e também é exposto como ferramenta (`@Tool`) para o fluxo de comando de voz
- Novo endpoint `GET /transactions/period?start=...&end=...`

Assim, além de consultar por categoria, o usuário agora pode consultar quanto movimentou em um intervalo de datas específico — por exemplo, "quanto gastei em julho".

## Como testar o fluxo principal

1. **Cadastrar um usuário**
   ```
   POST /users
   Content-Type: application/json

   { ... dados do usuário ... }
   ```

2. **Autenticar e obter o token JWT**
   ```
   POST /login
   Content-Type: application/json

   { "email": "...", "password": "..." }
   ```
   A resposta retorna o token JWT a ser usado nas próximas requisições.

3. **Criar uma transação** (autenticado)
   ```
   POST /transactions
   Authorization: Bearer <token>
   ```

4. **Consultar transações por categoria** (autenticado)
   ```
   GET /transactions/{category}
   Authorization: Bearer <token>
   ```

5. **Consultar transações por período** (autenticado)
   ```
   GET /transactions/period?start=2026-07-01&end=2026-07-31
   Authorization: Bearer <token>
   ```

6. **Comando por voz** (autenticado)
   ```
   POST /ai
   Authorization: Bearer <token>
   Content-Type: multipart/form-data
   ```
   Envie um arquivo de áudio; a API transcreve o comando, decide entre criar ou consultar uma transação, executa a ação e retorna a resposta em áudio (`audio/mp3`).

## O que eu aprendi

- Como implementar autenticação stateless com JWT em Spring Security, incluindo filtros customizados (`SecurityFilter`) e geração/validação de tokens
- Como manter a separação de camadas (domain/application/infrastructure) mesmo ao introduzir segurança, sem contaminar as regras de negócio com detalhes de infraestrutura
- Como o Spring AI expõe casos de uso da aplicação como ferramentas (`@Tool`) para que o modelo de IA possa decidir e executar ações reais dentro da API
- Como integrar autenticação com fluxos que envolvem múltiplas etapas assíncronas de IA (transcrição de áudio → decisão do modelo → execução do caso de uso → resposta em áudio)
- Como evoluir um modelo de domínio de forma incremental (adicionar um campo novo em `Transaction`/`TransactionEntity` sem quebrar o que já funcionava), propagando a mudança pelas camadas na ordem certa: domain → persistência → aplicação → controller
- Como usar *query methods* derivados do Spring Data JPA (`findAllByDateBetween`) para evitar escrever SQL manual em consultas simples
