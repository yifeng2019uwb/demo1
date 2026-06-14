# Database Setup

## Local Development — H2 (in-memory)

H2 is the default database for local development and tests. No installation required.

`src/main/resources/application.properties` points to an in-memory H2 instance:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

`driver-class-name` is intentionally omitted — Spring Boot auto-detects the driver from the URL. This also prevents a conflict if `SPRING_DATASOURCE_URL` is injected as an env var on a server (see [Troubleshooting](#troubleshooting)).

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

The app uses Spring profiles to switch databases. When `SPRING_PROFILES_ACTIVE=prod` is set, `application-prod.properties` is loaded automatically, overriding the H2 defaults.

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

`update` keeps existing data and only applies schema changes on startup. Tables are created automatically on first deploy if they don't exist.

The PostgreSQL driver is declared in `build.gradle`:

```groovy
runtimeOnly 'org.postgresql:postgresql'
```

### Setting the environment variables

All credentials and profile config are set via the DO App Platform UI — **nothing sensitive is committed to git**.

1. Go to **cloud.digitalocean.com → Apps → your app**
2. **Settings** tab → click your service component (`demo-api-service`)
3. Click **Edit** next to Environment Variables
4. Add all four of the following:

| Key | Value | Type |
|-----|-------|------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Plain |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:<port>/<database>?sslmode=require` | Plain |
| `SPRING_DATASOURCE_USERNAME` | your DB username | Plain |
| `SPRING_DATASOURCE_PASSWORD` | your DB password | **Encrypted** |

5. Click **Save** — DO redeploys automatically with the variables injected into the container.

> `SPRING_PROFILES_ACTIVE=prod` must be set here (not only in `app.yml`) to guarantee the prod profile is active and `application-prod.properties` is loaded.

### Deploy

```bash
git add build.gradle src/main/resources/application.properties src/main/resources/application-prod.properties
git commit -m "switch production database to DO PostgreSQL"
git push origin main
```

---

## Profile summary

| Profile | Database | `ddl-auto` | When used |
|---------|----------|------------|-----------|
| default | H2 in-memory | `create-drop` | local dev, tests |
| `prod`  | DO PostgreSQL | `update` | deployed on DO App Platform |

---

## Troubleshooting

### `Driver org.h2.Driver claims to not accept jdbcUrl, jdbc:postgresql://...`

**Cause:** `SPRING_PROFILES_ACTIVE=prod` was not set, so the app started with the default profile and loaded `application.properties`. The `SPRING_DATASOURCE_URL` env var overrode the H2 URL, but the H2 driver (configured explicitly via `driver-class-name`) cannot handle a PostgreSQL URL.

**Fix:**
1. Add `SPRING_PROFILES_ACTIVE=prod` to the DO App Platform environment variables UI.
2. Do not hardcode `spring.datasource.driver-class-name` in `application.properties` — let Spring auto-detect from the URL.
