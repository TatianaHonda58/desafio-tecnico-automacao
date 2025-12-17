Parte 0 – Teoria e Conceitos Fundamentais
  
1. Tipos de teste e prioridade no CI/CD
Resposta Objetiva:
●Teste de Unidade: Valida uma função/método isoladamente, usando mocks para depender apenas do código sob teste.
●Teste de Integração: Verifica a comunicação entre componentes (ex: API <-> DB) sem isolar dependências.
●Teste E2E: Simula o usuário final, validando o fluxo completo (UI -> Backend -> DB) em ambiente próximo à produção.
Prioridade no CI/CD:
Priorizar testes de unidade (rápidos, feedback imediato), depois integração, e por fim E2E (mais lentos e frágeis), que devem rodar em estágios posteriores (ex: pré-release).
Fundamentação: A pirâmide de testes orienta a estratégia: unitários formam a base (alta cobertura, baixo custo), enquanto E2E são o topo (baixa cobertura, alto valor de validação de fluxo).

2. Automação e ROI
Vantagens:
1.Redução de custo a longo prazo: Automatizar regressões elimina horas manuais.
2.Velocidade e eficiência: Execuções paralelas e CI/CD aceleram releases.
3.Confiabilidade: Scripts executam passos idênticos, eliminando falhas humanas.
Riscos:
1.Custo inicial alto: Ferramentas, infraestrutura e treinamento demandam investimento.
2.Manutenção contínua: Mudanças na UI ou contrato de API quebram scripts.
3.Cobertura limitada: Não substitui avaliação humana de UX, usabilidade ou cenários exploratórios.
Fundamentação: O ROI aparece com frequência de execução. Um script que roda 100x por semana paga seu custo em semanas. Mas se não for mantido, vira dívida técnica.

3. Teste de integração em microserviços
Desafios:
●Orquestração de múltiplos serviços.
●Inconsistência de dados entre ambientes.
●Flakiness por rede/latência.
●Quebras de contrato entre versões de APIs.
Boas práticas:
1.Contract Testing (PACT): Válida contratos entre provedor e consumidor sem subir todos os serviços.
2.Service Virtualization (WireMock/Testcontainers): Simula dependências externas para isolar testes em CI.
Fundamentação: Em microserviços, testar tudo junto é inviável. Contract testing garante que mudanças não quebram os consumidores. Testcontainers oferece isolamento com containers reais, aumentando a fidelidade.

4. Mecanismos de simulação
●Mock: Verifica interações (ex: verify(mock).metodo(...)). Foca no comportamento.
●Stub: Fornece respostas fixas (ex: when(service.get()).thenReturn("ok")). Foca no retorno.
●Spy: Chama a implementação real, mas permite interceptar chamadas específicas para forçar retornos ou verificar comportamentos.
Fundamentação: Use Mock quando precisar validar que um método foi chamado. Use Stub quando precisa controlar o retorno para testar lógica. Use Spy para testar classes complexas sem reescrever tudo.

5. Boas práticas de automação
●Estrutura clara de pacotes (src/test/java por feature).
●Nomeação expressiva de testes (testLoginValidoComPerfilUser).
●Isolamento via transações/rollback/Testcontainers.
●Reuso com Page Objects, Service Objects e Data Builders.
●Configurações externalizadas (properties/env vars).
●Determinismo com UUIDs e ON CONFLICT.
●Pirâmide de testes: muitos unitários, poucos E2E.
●Relatórios (JUnit XML, Allure) e CI integrado.
Fundamentação: Essas práticas garantem que a suíte seja legível, confiável e de fácil manutenção. Por exemplo, Page Object Model reduz duplicação; Test Data Builders tornam dados claros e reutilizáveis.

6. Gestão de dados de teste
●Trate dados como código: use migrations (Flyway/Liquibase) versionadas no VCS.
●Use bancos efêmeros (Testcontainers) ou snapshots para isolamento.
●Scripts idempotentes (INSERT ... ON CONFLICT DO NOTHING).
●Mascaramento de dados sensíveis (LGPD/GDPR).
●Rotinas de cleanup e governança (logs, expiração).
Fundamentação: Dados de teste devem ser rastreáveis, reproduzíveis e seguros. Bancos efêmeros garantem que cada teste inicie com estado limpo, evitando colisões entre execuções.
