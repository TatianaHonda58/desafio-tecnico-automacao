Parte A – Análise e Planejamento de Testes

Cenário 1: Login Válido (USER)
Objetivo: Usuário USER loga com credenciais válidas e acessa dashboard em ≤5s.
Tipo: Funcional/Positivo.
Camada: UI + API.
Automatizar? Sim. UI para fluxo completo; API para validar contrato.
Passos: Preencher credenciais → clicar entrar → validar URL /dashboard e elemento #welcome.
Critério: Status 200, token presente, elemento visível em ≤5s.
Cleanup: Rollback transacional ou uso de DB efêmero.

Cenário 2: Login Inválido (Senha Errada)
Objetivo: Credenciais inválidas retornam erro 401 e mensagem.
Tipo: Funcional/Negativo.
Camada: UI + API.
Automatizar? Sim.
Passos: Tentar login com senha errada → validar mensagem e status 401.
Critério: Sem sessão criada; auditoria registra tentativa com sucesso=false.
Cleanup: Remover registros de auditoria de teste.

Cenário 3: Login com Perfil VISITOR
Objetivo: Usuário VISITOR tenta acessar dashboard → recebe 403.
Tipo: Segurança/Negativo.
Camada: UI + API.
Automatizar? Sim.
Passos: Logar como VISITOR → validar que não redireciona e exibe mensagem 403.
Critério: Status 403; UI bloqueia acesso a /dashboard.
Cleanup: Rollback.

Cenário 4: Bloqueio Após 3 Tentativas
Objetivo: Após 3 tentativas falhas, conta é bloqueada (bloqueado=true) e retorna 423.
Tipo: Segurança.
Camada: API + DB + UI.
Automatizar? Sim. API para simular tentativas; UI para validar mensagem.
Passos: 3 POSTs com senha errada → tentar login válido → validar 423 e campo bloqueado=true.
Critério: Status 423; DB mostra bloqueado=true; auditoria tem 3 tentativas falhas.
Cleanup: Reset de campos tentativas e bloqueado via UPDATE ou TRUNCATE.

Cenário 5: Tempo de Carregamento do Dashboard (≤5s)
Objetivo: Dashboard carrega conteúdo em até 5 segundos.
Tipo: Não-funcional (Performance).
Camada: UI (Selenium).
Automatizar? Sim.
Passos: Após login, medir tempo até elemento #welcome estar visível.
Critério: Elemento visível e URL correta em ≤5s.
Cleanup: N/A.

Cenário 6: Concorrência de Logins
Objetivo: Múltiplos logins simultâneos não corrompem estado (ex: tentativas).
Tipo: Integração/Concorrência.
Camada: API.
Automatizar? Sim, com threads/executors.
Passos: Disparar 20 logins simultâneos → validar que cada um retorna status correto e auditoria tem 20 registros.
Critério: Nenhum deadlock; auditoria tem 20 entradas distintas.
Cleanup: TRUNCATE auditoria por test_id.

Cenário 7: Integridade de Auditoria (Dados Órfãos)
Objetivo: Não existirem registros de auditoria apontando para usuario_id inexistente.
Tipo: Integração/Dados.
Camada: DB (SQL).
Automatizar? Sim, rodando query e assert count=0.
Passos: Executar SELECT ... NOT IN (SELECT id FROM usuarios) → assert count = 0.
Critério: Zero registros órfãos.
Cleanup: Garantir FK com ON DELETE CASCADE ou remover órfãos manualmente.

Cenário 8: Idempotência em Criação
Objetivo: Reexecução de criação com mesmo payload não gera duplicatas.
Tipo: Integridade/Idempotência.
Camada: API + DB.
Automatizar? Sim.
Passos: Executar POST 2x → validar via SELECT que só existe 1 registro.
Critério: Count = 1.
Cleanup: DELETE por test_id.

Estrutura proposta:
- Maven, pacotes: pages, api, db, tests, utils
- Deps: Selenium, RestAssured, JUnit 5, Testcontainers, JDBC
- Config: test.properties (base URL, browser)
- CI: unitários em PR; E2E em branch de release
- Relatórios: JUnit XML + Allure
