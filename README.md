# DoeFlow

Projeto organizado em duas pastas:

- `back-end/`: API Spring Boot para campanhas de doacao.
- `front-end/`: aplicacao web do projeto.

O repositorio Git fica na raiz. Assim, ao abrir a pasta `DoeFlow` no VS Code, o Git enxerga as mudancas do back-end dentro de `back-end/` e tambem as futuras mudancas do front-end.

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
cd back-end
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd spring-boot:run
```

## Testes

```powershell
cd back-end
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
