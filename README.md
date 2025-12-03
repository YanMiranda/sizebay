# Rinha de Backend - Implementação Java (Sizebay)

Este repositório contém a implementação da API de transações financeiras para o desafio de alta concorrência **"Rinha de Backend (2024/Q1)"**.

O objetivo foi desenvolver uma API resiliente capaz de suportar tráfego massivo e concorrente, respeitando limites estritos de hardware (1.5 CPU e 550MB de RAM).

## 🏆 Resultados Obtidos
- **Resiliência Total:** O sistema manteve-se estável sem *CrashLoopBackOff* (OOM Kill) ou erros de *Bad Gateway* (502) sob estresse máximo.
- **Consistência:** Validação rigorosa de saldos e limites utilizando mecanismos atômicos de banco de dados.

---

## 🛠 Tecnologias e Arquitetura

A solução foi desenhada focando em **reduzir o overhead da JVM** e delegar a complexidade transacional para quem faz isso melhor: o Banco de Dados.

- **Linguagem:** Java 17 (LTS)
- **Framework:** Spring Boot 3.3
- **Banco de Dados:** PostgreSQL 16
- **Proxy/Load Balancer:** Nginx
- **Containerização:** Docker & Docker Compose

### Diferenciais da Implementação

1.  **Stored Procedures (PL/pgSQL):**
    * A lógica de "buscar cliente -> validar saldo -> atualizar saldo -> salvar transação" foi encapsulada em uma função nativa do banco (`create_transaction`).
    * **Por que?** Isso elimina o *Round-Trip* de rede entre a Aplicação e o Banco, reduzindo drasticamente o tempo de bloqueio (*lock*) das linhas e liberando as threads do Tomcat mais rapidamente.

2.  **Tuning de Nginx (Retry Policy):**
    * Configuração agressiva de `proxy_next_upstream` e `timeouts`. Se uma instância da API estiver ocupada, o Nginx redireciona a requisição imediatamente para a réplica vizinha, garantindo que o usuário nunca receba um erro 502.

3.  **Otimização de JVM & JDBC:**
    * Uso de tipos fixos (`CHAR(1)`) mapeados corretamente com `@JdbcTypeCode`.
    * Tratamento de exceções sem geração de *Stack Trace* (`fillInStackTrace`) para economizar CPU.
    * Uso de tabelas `UNLOGGED` no Postgres para maximizar a taxa de escrita (Write Throughput).

---

## 🚀 Como Rodar Localmente

### Pré-requisitos
- Docker e Docker Compose instalados.
- Java 17+ (opcional, apenas para rodar testes unitários fora do Docker).

### Executando a Aplicação
Para subir toda a infraestrutura (2 APIs + Nginx + Postgres):

```bash
docker-compose up --build
```
