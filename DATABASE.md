# Database Setup

## Local Development — H2 (in-memory)

H2 is the default database for local development and tests. No installation required.

`src/main/resources/application.properties` points to an in-memory H2 instance:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

`create-drop` means the schema is created on startup and dropped on shutdown — data does not persist between runs.

H2 is declared as a test-only dependency in `build.gradle`:

```groovy
testRuntimeOnly 'com.h2database:h2'
```

### Run locally

```bash
make run       # starts the app on port 8080
make test      # runs all tests against H2
```

---

## Production — DigitalOcean Managed PostgreSQL

The app uses Spring profiles to switch databases. `SPRING_PROFILES_ACTIVE=prod` is set in `.do/app.yml`, so `application-prod.properties` is loaded automatically at runtime on DO.

### How it works

`src/main/resources/application-prod.properties` reads connection details from environment variables:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

`update` keeps existing data and only applies schema changes on startup.

The PostgreSQL driver is declared in `build.gradle`:

```groovy
runtimeOnly 'org.postgresql:postgresql'
```

### Setting the environment variables

Credentials are **not stored in `app.yml` or any committed file**. They are set via the DO App Platform UI so they never touch git.

1. Go to **cloud.digitalocean.com → Apps → your app**
2. **Settings** tab → click your service component (`demo-api-service`)
3. Click **Edit** next to Environment Variables
4. Add the following:

| Key | Value | Type |
|-----|-------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:<port>/<database>?sslmode=require` | Plain |
| `SPRING_DATASOURCE_USERNAME` | your DB username | Plain |
| `SPRING_DATASOURCE_PASSWORD` | your DB password | **Encrypted** |

5. Click **Save** — DO redeploys automatically with the new variables injected into the container.

> The `SPRING_PROFILES_ACTIVE=prod` variable in `.do/app.yml` triggers the profile switch. The three `SPRING_DATASOURCE_*` variables are resolved at runtime from the values set in the UI above.

### Deploy

```bash
git add build.gradle src/main/resources/application-prod.properties .do/app.yml
git commit -m "switch production database to DO PostgreSQL"
git push origin main
```

---

## Profile summary

| Profile | Database | `ddl-auto` | When used |
|---------|----------|------------|-----------|
| default | H2 in-memory | `create-drop` | local dev, tests |
| `prod`  | DO PostgreSQL | `update` | deployed on DO App Platform |
