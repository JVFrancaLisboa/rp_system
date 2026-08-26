# rp_system

Sistema base em **Spring Boot (Java 17)** com interface web usando **Bootstrap 5** e persistência em **MySQL**.

## Tecnologias

- Spring Boot 3
- Thymeleaf
- Bootstrap 5 (CDN)
- Spring Data JPA
- MySQL Connector/J
- Maven

## Executar

```bash
mvn spring-boot:run
```

Configurações padrão de banco (`src/main/resources/application.properties`):

- `DB_URL` (default: `jdbc:mysql://localhost:3306/rp_system?...`)
- `DB_USERNAME` (default: `root`)
- `DB_DRIVER` (default: `com.mysql.cj.jdbc.Driver`)
- `JPA_DIALECT` (default: `org.hibernate.dialect.MySQLDialect`)

## Testes

```bash
mvn test
```