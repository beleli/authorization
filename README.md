# Blitech authorization-service

## Descrição

O Blitech authorization-service é uma API de autorização desenvolvido em Java utilizando o Spring Boot.<br>
Ela prove funcionalidades de autenticação e autorização para aplicações utilizando tokens opacos JWT. Incluindo a geração e rotacionamento das chaves de criptogradia RS256.
Existem 3 modelos de autenticação:
- Administrador (usuário de aplicação) que permite o gerencimento do seu controle de acesso e de usuários de serviço.
- Usuário de Serviço que permite a autenticação de aplicação para aplicação.
- Usuário de Domínio que um usuário do AD (domian controler) ter acesso aos recursos de uma aplicação.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- JaCoCo
- SonarQube
- JJWT (Java JWT)
- AWS SDK (Secrets Manager)
- Micrometer OpenTelemetry
- Grafana
- SpringDoc OpenAPI
- Docker

## Requisitos

- JDK 17
- Gradle
- PostgreSQL
- SonarQube (opcional, para análise de código)

## Build e Execução

### Docker
Subir o docker-compose que está na raiz do projeto:
```bash
docker compose up
```

### Variáveis de Ambiente
Configure as seguintes variáveis de ambiente:  

AUTHORIZATION_SECRETS_MANAGER_IMPL: localstack<br>
SPRING_PROFILES_ACTIVE: local

### Compilar o Projeto

Para compilar o projeto, execute o seguinte comando:
```bash
./gradlew clean build
```

### Executar os Testes

Para executar os testes, utilize o comando:
```bash
./gradlew test
```

### Executar a Aplicação
Para iniciar a aplicação, execute:
```bash
./gradlew bootRun
```

## Contribuição

Faça um fork do projeto.<br>
Crie uma branch para sua feature (git checkout -b feature/nova-feature).<br>
Commit suas mudanças (git commit -am 'Adiciona nova feature').<br>
Faça o push para a branch (git push origin feature/nova-feature).<br>
Crie um novo Pull Request.<br>