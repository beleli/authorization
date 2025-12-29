# Arquitetura do projeto Authorization

Resumo curto

O projeto usa Spring Boot e está organizado em camadas, com forte separação entre apresentação (API), camada de aplicação/domínio e infraestrutura (adapters). O estilo é uma arquitetura em camadas com influência de Ports & Adapters (Hexagonal) e práticas de DDD leve (entidades, serviços e repositórios).

Camadas e papéis

- API / Presentation
  - Local: `src/main/java/br/com/blitech/authorization/api/*`
  - Contém controllers REST, filtros (ex.: JWT) e handlers de exceção.
  - Arquivos representativos: `api/v1/controller/ServiceUserController.java`, `api/exceptionhandler/ApiExceptionHandler.java`.

- Application / Domain
  - Local: `src/main/java/br/com/blitech/authorization/domain/*`
  - Contém entidades, serviços de domínio e interfaces de repositório.
  - Arquivos representativos: `domain/entity/ServiceUser.java`, `domain/service/ServiceUserService.java`.

- Infrastructure / Adapters
  - Local: `src/main/java/br/com/blitech/authorization/infrastructure/*`
  - Implementações concretas (Redis, LDAP, etc.) que atendem às interfaces do domínio.
  - Arquivos representativos: `infrastructure/cache/RedisAdapter.java`, `infrastructure/domain/LdapAdapter.java`.

- Core / Config
  - Local: `src/main/java/br/com/blitech/authorization/core/*`
  - Configurações, utilitários, resolvers de segredos e customizações (Jackson, i18n, cache).
  - Arquivos representativos: `core/config/DataSourceConfig.java`, `core/io/SecretsManagerProtocolResolver.java`, `core/message/Messages.java`.

Frameworks e integrações chave

- Spring Boot (entrada em `AuthorizationApplication.java`).
- Segurança JWT: `api/filter/JwtAuthenticationFilter.java`, `api/security/*`.
- OpenAPI/Swagger: `api/openapi/OpenApiConfig.java` e interfaces `api/v1/openapi/*`.
- Cache/Redis, LDAP, SecretsManager: adapters sob `infrastructure/*` com configurações em `core/config`.

Preocupações transversais identificadas

- Segurança (JWT), Logging (aspects), Validação (aspects), I18n (`core/message/Messages.java`), Caching (Redis/Memory adapters), Tratamento centralizado de exceções.

Evidências (exemplos de arquivos)

- Controllers: `src/main/java/br/com/blitech/authorization/api/v1/controller/*Controller.java`
- Domain services: `src/main/java/br/com/blitech/authorization/domain/service/*Service.java`
- Repositories (interfaces): `src/main/java/br/com/blitech/authorization/domain/repository/*Repository.java`
- Adapters infra: `src/main/java/br/com/blitech/authorization/infrastructure/*/*Adapter.java`
- Configs: `src/main/java/br/com/blitech/authorization/core/config/*Config.java`

Diagrama textual simples

[Clients]
  -> [API / Controllers / Filters / OpenAPI]
      -> [Application Layer / DTOs / Assemblers]
          -> [Domain Services / Entities / Repository Interfaces]
              -> [Infrastructure Adapters (Redis, LDAP, JPA, SecretsManager)]
                  -> [External systems: DB, Redis, LDAP, SecretsManager]

Trecho PlantUML (opcional)

```
@startuml
title Arquitetura - Authorization
[Clients] --> [API/Controllers]
[API/Controllers] --> [Application/Assemblers]
[Application/Assemblers] --> [Domain/Services]
[Domain/Services] --> [Infra/Adapters]
[Infra/Adapters] --> [External Systems]
@enduml
```

Recomendações / próximos passos

1. Adicionar `ARCHITECTURE.md` (feito) e um diagrama PlantUML no diretório `docs/` para documentação visual (baixo esforço).
2. Cobertura de testes de integração para adapters (usar Testcontainers ou mocks) para validar contratos com DB/Redis/LDAP (médio esforço).
3. Considerar modularização Gradle (api, domain, infrastructure, core) se o projeto crescer para reduzir tempo de build e acoplamento (alto esforço).

Se quiser, eu adiciono também um arquivo `docs/architecture.puml` com o PlantUML pronto, ou crio uma versão detalhada do `ARCHITECTURE.md` com mais exemplos e vínculos para os arquivos fonte — confirme qual opção prefere.
