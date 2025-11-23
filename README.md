# Sistema de Solicitação de Acesso a Módulos

## Descrição do Projeto

Aplicação para gerenciamento de solicitações de acesso a módulos corporativos, com concessão automática baseada em regras de negócio.

## Tecnologias Utilizadas e Versões

- Java 21
- Spring Boot 3.3.4
- Spring Data JPA
- Spring Validation
- PostgreSQL 17 (produção)
- H2 (testes)
- Maven 3.9+
- Docker / Docker Compose
- Nginx (proxy/LB)
- Lombok
- Flyway (migrações)
- JUnit 5, Mockito, MockMvc, Spring Security Test, JaCoCo

## Pré-requisitos

- Docker e Docker Compose instalados
- Maven para build local (opcional)

## Como Executar Localmente com Docker

1. Clone o repositório
2. Rode `docker-compose up -d`
3. Acesse API via [http://localhost](http://localhost/) (Nginx proxy)
4. Swagger: http://localhost/swagger-ui.html (Deve passar as portas, 8081, 8082 ou 8083)

## Como Executar os Testes

- Rode `mvn test` para unitários/integração
- Cobertura: `mvn jacoco:report` (relatório em target/site/jacoco/index.html)

## Como Visualizar Relatório de Cobertura

Após `mvn jacoco:report`, abra target/site/jacoco/index.html.  Exporte para PDF se necessário.

## Credenciais para Teste

- Usuários iniciais: [ti@user.com], [financeiro@user.com], [rh@user.com], [operacoes@user.com]

## Exemplos de Requisições

- POST /auth/login: {"email": "[ti@user.com]", "tiuser": "password"} -> Retorna JWT
- POST /requests: (com Authorization Bearer JWT) {"moduleIds": [1], "justification": "Preciso para trabalho", "urgent": false}
- GET /requests: ?page=0&status=ATIVO
- Use Postman/Curl

## Arquitetura da Solução

Camadas: Controller -> Service -> Repository. Segurança JWT. Infra: 3 apps balanceadas por Nginx, banco Postgres.
Diagrama (texto):
User -> Nginx (LB) -> App1/2/3 -> Postgres
Decisões: DTOs para desacoplamento; Flyway para migrações; JaCoCo para cobertura rigorosa.

## Decisões Técnicas Relevantes

- SOLID aplicado para modularidade.
- Records em DTOs (Java 21) para imutabilidade.
- Testes sem any(), com verify() para precisão.
- Multi-stage Dockerfile para otimização.
- Profiles para ambientes dev/prod.
