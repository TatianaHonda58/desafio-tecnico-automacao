# Desafio Técnico – Analista de Automação de Testes

Este repositório contém a entrega completa do desafio técnico.

## 📦 Conteúdo

- Parte A: Análise de 8+ cenários de teste (cenarios-de-teste.md)
- Parte B: Automação UI com Selenium (Page Object + WebDriverWait)
- Parte C: Automação API com RestAssured (200 / 401 / 403 / 423)
- Parte E: Consultas SQL e diagnóstico (sql/respostas-sql.md)

## 🧪 Tecnologias

- Java
- Maven
- Selenium WebDriver
- RestAssured
- JUnit 5
- PostgreSQL

## ▶️ Como executar o projeto

### Pré-requisitos:
- Java 11+
- Maven
- ChromeDriver configurado

### Executar testes de UI:

```bash
mvn clean test -Dtest=LoginUITest

Executar testes de API:
mvn clean test -Dtest=LoginApiTest

