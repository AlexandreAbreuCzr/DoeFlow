# DoeFlow

API Spring Boot para campanhas de doacao com cadastro de usuarios, autenticacao JWT, criacao e aprovacao de campanhas, registro de doacoes e acompanhamento do progresso arrecadado.

## Requisitos

- Java 17
- Maven Wrapper incluido no projeto
- PostgreSQL para execucao local

## Variaveis de ambiente

```bash
DB_URL=jdbc:postgresql://localhost:5432/doeflow
DB_USERNAME=postgres
DB_PASSWORD=postgres
SECRET=troque-por-uma-chave-com-mais-de-32-caracteres
JWT_ISSUER=DoeFlow
JWT_EXPIRATION_HOURS=2
```

## Rodando

No Windows, caso o Java padrao esteja em outra versao, aponte o `JAVA_HOME` para um JDK 17 antes de rodar:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd spring-boot:run
```

## Testes

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean test
```

## Principais rotas

- `POST /auth/register`
- `POST /auth/login`
- `GET /campaign`
- `POST /campaign`
- `PATCH /campaign/{id}`
- `PATCH /campaign/{id}/approve`
- `PATCH /campaign/{id}/refuse`
- `POST /donation`
- `GET /donation/my`
- `GET /users/me`
