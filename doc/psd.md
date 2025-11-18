# Visão Geral do Produto

O objetivo deste SaaS é permitir que usuários registrem, organizem e acompanhem suas leituras de forma simples e eficiente. O sistema fornecerá funcionalidades básicas como cadastro de livros, progresso de leitura e anotações, com uma interface moderna e responsiva.

Este documento foi dividido em dois: Front-end e Back-end, cada um detalhando suas responsabilidades e tecnologias.

---

## Back-end — Especificação

### Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL (banco relacional)
- Controle de versão: Git + GitHub
- Deploy futuro: AWS, Railway ou Render

### Funcionalidades do Back-end

- CRUD de usuários com autenticação JWT
- CRUD de livros (somente do usuário logado)
- Endpoint para atualizar progresso de leitura
- CRUD de anotações vinculadas a livros
- Validações de dados (ex.: current_page <= total_pages)
- Segurança e rotas protegidas

### MVP — Back-end

Para o MVP, o back-end deve conter:

- Cadastro e login de usuários com JWT
- CRUD completo de livros do usuário
- CRUD de anotações vinculadas a livros
- Endpoint para atualizar progresso de leitura
- Validações básicas e integridade dos dados

### Banco de Dados (compartilhado)

Tabelas essenciais para ambos front e back:

- users
- books
- notes
- progress (opcional, pode ser campo em "books" no MVP)

### Futuras Melhorias (Pós-MVP)

- Histórico de progresso (gráficos)
- Metas de leitura mensais e anuais
- Busca avançada e filtros
- Compartilhamento de livros e anotações
- IA para resumo de anotações
- Aplicativo mobile

## Considerações Finais

O MVP proposto separa claramente responsabilidades entre front-end e back-end, garantindo que cada camada forneça a funcionalidade mínima necessária para o usuário acompanhar suas leituras e anotações, abrindo caminho para futuras melhorias e escalabilidade.