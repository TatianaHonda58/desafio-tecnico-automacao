# Parte E – SQL e Banco de Dados (PostgreSQL)

## Exemplo 1 – Propósito e Correção

### Consulta: 
SELECT u.username, COUNT(a.id) AS total_logins 
FROM usuarios u
LEFT JOIN auditoria_login a ON a.usuario_id = u.id AND a.sucesso = true
WHERE u.perfil = ' ADMIN '
GROUP BY u.username 
HAVING COUNT(a.id) > 5 
ORDER BY total_logins DESC;

1. Propósito:
- Listar usuários com perfil ADMIN que realizaram mais de 5 logins bem-sucedidos, ordenados pelo número total de logins (decrescente).

2. Erros:
- Erro Sintático/Semântico: Espaços extras em ' ADMIN ' podem causar falhas se os dados forem armazenados como 'ADMIN' sem espaços.
- Potencial Melhoria: Usar TRIM(u.perfil) = 'ADMIN' ou normalizar os dados no banco.

3. Cenário de Teste:
- Positivo: Usuário ADMIN com 6+ logins bem-sucedidos → deve aparecer.
- Negativo: Usuário ADMIN com ≤5 logins → não deve aparecer.
- Dados: Usuário com perfil 'ADMIN ' (com espaço) → não deve retornar resultados, revelando bug de normalização.

## Exemplo 2 – Identificação de Erro Lógico
### Consulta:
SELECT * FROM usuarios WHERE bloqueado='false';

1. Erro:
Em PostgreSQL, bloqueado é provavelmente uma coluna boolean. Comparar com string 'false' é incorreto.

2. Correção:
SELECT * FROM usuarios WHERE bloqueado = false;
-- ou
SELECT * FROM usuarios WHERE NOT bloqueado;

3. Detecção por Teste Automatizado: 
Um teste de integração executaria essa query e esperaria:
- Lançar exceção PSQLException se o tipo for incompatível.
- Retornar lista vazia se o driver converter implicitamente 'false' para NULL.

## Exemplo 3 – Verificação de Bloqueio
### Consulta: 
SELECT u.username, u.tentativas,
  CASE WHEN u.tentativas >= 3 THEN 'BLOQUEADO' ELSE 'ATIVO' END AS status
FROM usuarios u;

1. Cenário “BLOQUEADO”:
- Usuário realiza 3 tentativas de login com senha incorreta → sistema incrementa tentativas para 3 → consulta retorna status = 'BLOQUEADO'.

2. Validação de Consistência:
Após simular 3 tentativas via API:
- Executar a consulta → validar status = 'BLOQUEADO'.
- Consultar diretamente SELECT bloqueado FROM usuarios WHERE username = '...'; → esperar true.
- Validar que o endpoint /login retorna 423 ao tentar com credenciais válidas.

3. Limpeza da Base:
- Opção 1: Transação com rollback (ideal para Testcontainers).
- Opção 2: Atualizar o registro:
UPDATE usuarios SET tentativas = 0, bloqueado = false WHERE username = 'test_user';
- Opção 3: Deletar o registro de teste pelo test_id ou username único.

## Exemplo 4 – Erro Conceitual em Junção
### Consulta:
SELECT u.username, a.data_evento 
FROM usuarios u, auditoria_login a 
WHERE u.id = a.usuario_id(+);

1. Erro:
- A sintaxe (+) é específica do Oracle para outer join. PostgreSQL não suporta essa notação.

2. Correção:
SELECT u.username, a.data_evento 
FROM usuarios u
LEFT JOIN auditoria_login a ON u.id = a.usuario_id;

3. Erro Retornado pelo PostgreSQL:
ERROR: syntax error at or near "("
... WHERE u.id = a.usuario_id(+)

## Exemplo 5 –  Integridade e Dados Órfãos
### Consulta:
SELECT a.id, a.usuario_id 
FROM auditoria_login a 
WHERE a.usuario_id NOT IN (SELECT id FROM usuarios);

1. Propósito:
- Identificar registros de auditoria cujo usuario_id não corresponde a nenhum usuário existente — ou seja, dados órfãos.

2. Contribuição para Testes de Integração:
- Garante a integridade referencial entre auditoria_login e usuarios. É crucial após operações de exclusão de usuários ou migrações de dados.

3. Cenário de Teste:
- Criar um registro em auditoria_login vinculado a um usuario_id existente.
- Excluir o usuário sem ON DELETE CASCADE.
- Executar a consulta → deve retornar 1 registro (órfão).
- O teste falha se o resultado não for vazio, exigindo correção no modelo de dados.

4. Prevenção no Banco:
- Definir chave estrangeira com ON DELETE CASCADE:
ALTER TABLE auditoria_login 
ADD CONSTRAINT fk_usuario 
FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
