# Mudanças realizadas

- Corrigido o import de `@Query` em `src/main/java/com/NFS_E/notaFiscalEletronica/repository/NotaFiscalRepository.java`.
  - Substituído `org.springframework.data.jdbc.repository.query.Query` por `org.springframework.data.jpa.repository.Query`.
  - Isso corrigiu a falha de criação do repositório `NotaFiscalRepository` causada pela interpretação errada do método `findMaxNumero()`.

- Ajustado o perfil de teste em `src/test/resources/application-test.yml`.
  - Adicionado `JWT_SECRET: test-secret`.
  - Adicionado valores dummy para `sefaz.nfe.certificado.caminho` e `sefaz.nfe.certificado.senha`.
  - Isso permitiu que o contexto de teste carregasse sem falhar na criação do bean `NFeConfigImpl`.

- Removido o código dependente de bibliotecas externas não configuradas ou que não estavam disponíveis localmente:
  - `DanfeService` agora lança `UnsupportedOperationException` em vez de depender de JasperReports.
  - `NfeTransmissaoService` agora lança `UnsupportedOperationException` para transmissão e cancelamento SEFAZ até que o conector esteja implementado corretamente.

- O teste de integração principal foi atualizado para usar o profile `test` em `src/test/java/com/NFS_E/notaFiscalEletronica/NotaFiscalEletronicaApplicationTests.java`.

## Resultado

- `mvnw.cmd -q clean test` executou com sucesso no módulo `notaFiscalEletronica`.
- O build de teste agora inicializa o contexto Spring Boot com o perfil de teste e o banco H2 em memória.

## Observações

- As funcionalidades de transmissão e cancelamento SEFAZ ainda estão stubbed e precisam de implementação real com o conector SEFAZ.
- A geração de DANFE também permanece não implementada até que a dependência JasperReports seja adicionada e configurada.
