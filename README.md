# 🍔 Delivery System

Projeto desenvolvido para a disciplina de **Java Advanced - Segundo Semestre**.

O sistema representa uma aplicação de delivery construída com arquitetura de microsserviços, utilizando Eureka para descoberta de serviços, Load Balancing, Retry, RabbitMQ, processamento assíncrono de avaliações, controle de concorrência de estoque e Spring AI.

---

## 👩‍💻 Integrantes

- **Nome:** Emily Maria de Oliveira Macedo
- **RM:** 554882

---

## 🛠️ Tecnologias utilizadas

- Java 25
- Spring Boot 4
- Spring Cloud 2025.1.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Cloud Netflix Eureka
- Spring Cloud LoadBalancer
- Spring Resilience
- RabbitMQ
- Docker
- H2 Database
- Spring AI
- Lombok
- Gradle

---

# 🏗️ Arquitetura

O projeto utiliza um monorepo contendo quatro aplicações Gradle independentes:

```text
delivery_system/
│
├── eureka-server/
├── order-service/
├── payment-service/
├── review-service/
├── docker-compose.yml
└── README.md
```

## Serviços

| Serviço | Porta | Responsabilidade |
|---|---:|---|
| Eureka Server | 8761 | Service Discovery |
| Order Service | 8080 | Cardápio, pedidos, estoque, avaliações e assistente IA |
| Payment Service | 8081 | Processamento de pagamentos |
| Payment Service | 8082 | Segunda instância para Load Balancing |
| Review Service | 8083 | Processamento e ranking de avaliações |
| RabbitMQ | 5672 | Mensageria |
| RabbitMQ Management | 15672 | Interface administrativa |

---

# 🐇 RabbitMQ

O RabbitMQ é executado através do Docker Compose.

Na raiz do projeto:

```bash
docker compose up -d
```

Para verificar o container:

```bash
docker ps
```

A interface administrativa do RabbitMQ fica disponível em:

```text
http://localhost:15672
```

Credenciais padrão:

```text
Username: guest
Password: guest
```

A mensageria utiliza:

```text
Exchange: delivery.exchange
Queue: reviews.queue
Routing Key: reviews.new
```

A fila é durável e está associada ao `TopicExchange` através de um binding explícito.

---

# 🔎 Eureka Server

Entre na pasta:

```bash
cd eureka-server
```

No Windows:

```powershell
.\gradlew bootRun
```

O painel do Eureka estará disponível em:

```text
http://localhost:8761
```

Os serviços registrados são:

```text
ORDER-SERVICE
PAYMENT-SERVICE
REVIEW-SERVICE
```

O `PAYMENT-SERVICE` possui duas instâncias registradas.

---

# 📦 Order Service

Entre na pasta:

```bash
cd order-service
```

Execute:

```powershell
.\gradlew bootRun
```

O serviço será iniciado em:

```text
http://localhost:8080
```

O Order Service é responsável por:

- consulta do cardápio;
- criação de pedidos;
- controle de estoque;
- lock pessimista;
- integração com o Payment Service;
- publicação de avaliações no RabbitMQ;
- assistente utilizando Spring AI.

---

# 💳 Payment Service

O Payment Service simula uma API de pagamento instável.

Aproximadamente 50% das chamadas retornam erro `500`, permitindo testar Load Balancing e Retry.

## Primeira instância

Entre na pasta:

```bash
cd payment-service
```

Execute:

```powershell
.\gradlew bootRun
```

A primeira instância utiliza:

```text
http://localhost:8081
```

## Segunda instância

Em outro terminal, ainda dentro de `payment-service`:

```powershell
.\gradlew bootRun --args="--server.port=8082"
```

A segunda instância utiliza:

```text
http://localhost:8082
```

As duas instâncias são registradas no Eureka como:

```text
PAYMENT-SERVICE
```

A resposta de pagamento informa qual instância processou a requisição.

Exemplo:

```json
{
  "status": "APPROVED",
  "instance": 8081
}
```

---

# ⭐ Review Service

Entre na pasta:

```bash
cd review-service
```

Execute:

```powershell
.\gradlew bootRun
```

O serviço será iniciado em:

```text
http://localhost:8083
```

O Review Service:

- recebe avaliações através do RabbitMQ;
- utiliza `@RabbitListener`;
- acumula avaliações em um `ConcurrentHashMap`;
- evita gravação direta no banco para cada mensagem;
- realiza flush do buffer a cada 5 segundos;
- grava os resultados acumulados no banco H2;
- disponibiliza um ranking de pratos por média de avaliações.

---

# 📋 Endpoints

## Order Service - Porta 8080

### Listar pratos

```http
GET /dishes
```

Exemplo:

```text
GET http://localhost:8080/dishes
```

---

### Buscar prato por ID

```http
GET /dishes/{id}
```

Exemplo:

```text
GET http://localhost:8080/dishes/1
```

---

### Criar pedido

```http
POST /orders
```

Exemplo de requisição:

```json
{
  "dishId": 1,
  "quantity": 1
}
```

Exemplo de resposta:

```json
{
  "id": 1,
  "dishId": 1,
  "quantity": 1,
  "totalPrice": 39.90,
  "status": "CONFIRMED",
  "createdAt": "2026-10-09T13:00:00"
}
```

Possíveis respostas:

```text
201 Created
400 Bad Request
404 Not Found
409 Conflict
502 Bad Gateway
```

---

### Buscar pedido

```http
GET /orders/{id}
```

Exemplo:

```text
GET http://localhost:8080/orders/1
```

---

### Publicar avaliação

```http
POST /reviews
```

Exemplo:

```json
{
  "dishId": 1,
  "rating": 5,
  "comment": "Great"
}
```

Resposta esperada:

```text
202 Accepted
```

A avaliação não é gravada diretamente no banco pelo Order Service.

Ela é publicada no RabbitMQ e processada posteriormente pelo Review Service.

---

### Assistente com Spring AI

```http
POST /assistant
```

Exemplo:

```json
{
  "question": "Tem prato vegetariano até R$ 40?"
}
```

Exemplo de resposta:

```json
{
  "answer": "..."
}
```

O assistente utiliza o cardápio atual armazenado no banco.

Ele responde apenas perguntas relacionadas ao restaurante, pratos, preços, ingredientes, estoque e sugestões.

Perguntas fora desse contexto são recusadas educadamente.

---

# 📊 Review Service - Porta 8083

### Ranking de avaliações

```http
GET /reviews/ranking
```

Exemplo:

```text
GET http://localhost:8083/reviews/ranking
```

Exemplo de resposta:

```json
[
  {
    "dishId": 1,
    "dishName": "House Burger",
    "average": 5.0,
    "count": 1
  }
]
```

Os pratos são retornados ordenados pela média das avaliações.

---

# 🔒 Controle de concorrência

A criação de pedidos utiliza:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

junto com:

```java
@Transactional
```

O objetivo é impedir que múltiplas requisições concorrentes vendam uma quantidade maior do que o estoque disponível.

O prato promocional inicia com:

```text
stock = 10
```

O sistema garante que o estoque não fique negativo mesmo quando várias requisições forem feitas simultaneamente.

---

# ⚖️ Load Balancing

O Order Service não utiliza URLs fixas para acessar o Payment Service.

A chamada é feita através do nome registrado no Eureka:

```text
http://PAYMENT-SERVICE/payments
```

O `RestTemplate` utiliza:

```java
@LoadBalanced
```

permitindo que as chamadas sejam distribuídas entre:

```text
8081
8082
```

---

# 🔁 Retry

A comunicação com o Payment Service utiliza Retry através de um `PaymentClient` separado.

A implementação possui:

- quantidade máxima de tentativas;
- delay;
- backoff exponencial;
- multiplier;
- jitter;
- maxDelay.

Caso uma tentativa falhe, uma nova chamada é realizada automaticamente.

Se todas as tentativas falharem, o Order Service retorna:

```text
502 Bad Gateway
```

Exemplo:

```json
{
  "error": "Payment service unavailable"
}
```

Como a criação do pedido ocorre dentro de uma transação, o estoque é revertido caso o pagamento não seja concluído.

---

# 📨 Mensageria

As avaliações são processadas de maneira assíncrona utilizando RabbitMQ.

Fluxo:

```text
POST /reviews
      ↓
Order Service
      ↓
delivery.exchange
      ↓
reviews.new
      ↓
reviews.queue
      ↓
Review Service
      ↓
ConcurrentHashMap
      ↓
Flush a cada 5 segundos
      ↓
H2
      ↓
GET /reviews/ranking
```

Isso evita gravar individualmente no banco cada avaliação recebida durante períodos de grande volume.

---

# 🤖 Spring AI

O Order Service possui um assistente utilizando Spring AI.

O cardápio atual é consultado no banco e incluído no prompt enviado ao modelo.

O assistente recebe instruções para:

- responder em português;
- utilizar respostas curtas;
- responder com base no cardápio atual;
- não inventar produtos;
- recusar perguntas fora do contexto do restaurante.

---

## 🔐 Configuração da API Key

A chave da API não é armazenada no repositório.

O `application.properties` utiliza variável de ambiente:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

Antes de iniciar o Order Service no PowerShell:

```powershell
$env:OPENAI_API_KEY="SUA_CHAVE"
```

Nunca envie a chave da API para o GitHub.

---

# ▶️ Ordem recomendada para execução

## 1. RabbitMQ

Na raiz:

```powershell
docker compose up -d
```

## 2. Eureka Server

```powershell
cd eureka-server
.\gradlew bootRun
```

## 3. Order Service

```powershell
cd order-service
.\gradlew bootRun
```

## 4. Payment Service - 8081

```powershell
cd payment-service
.\gradlew bootRun
```

## 5. Payment Service - 8082

Em outro terminal:

```powershell
cd payment-service
.\gradlew bootRun --args="--server.port=8082"
```

## 6. Review Service

```powershell
cd review-service
.\gradlew bootRun
```

Depois, acesse:

```text
http://localhost:8761
```

e confirme se os serviços estão registrados no Eureka.

---

# 🧪 Testes realizados

Durante o desenvolvimento foram testados:

- consulta de pratos;
- consulta de prato por ID;
- criação de pedidos;
- validação de quantidade;
- prato inexistente;
- estoque insuficiente;
- pagamento aprovado;
- falha simulada de pagamento;
- duas instâncias do Payment Service;
- Load Balancing;
- Retry;
- retorno `502`;
- rollback do estoque após falha no pagamento;
- publicação de avaliações;
- retorno `202 Accepted`;
- consumo de mensagens pelo RabbitMQ;
- processamento assíncrono;
- flush periódico;
- ranking de avaliações;
- assistente com Spring AI.

---

# 📌 Observações

- Todo o código da aplicação foi desenvolvido em inglês.
- Os microsserviços utilizam Eureka para descoberta de serviços.
- Não existe URL `localhost` hardcoded para comunicação entre microsserviços.
- RabbitMQ é executado utilizando Docker Compose.
- O Payment Service possui duas instâncias.
- A chave utilizada pelo Spring AI não é armazenada no repositório.
- As avaliações são processadas de maneira assíncrona.
- O estoque utiliza controle de concorrência com lock pessimista.