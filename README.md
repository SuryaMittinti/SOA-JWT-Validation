# JWT Validation and Claims Extraction — Spring Boot

A complete Java 17 / Spring Boot project that validates signed JWTs, rejects tampered/expired tokens, extracts claims, exposes REST endpoints, and includes Swagger UI.

## Requirements
- JDK 17+
- Maven 3.8+
- Internet access on first build to download Maven dependencies

## Run
From this project folder:

```bash
mvn spring-boot:run
```

Open Swagger UI: http://localhost:8080/swagger-ui.html

## Test with Swagger UI

1. Run `POST /api/demo-token` with subject `ramsai`. Copy the `token` from the response.
2. Run `POST /api/validate` with this JSON body:
   ```json
   { "token": "PASTE_TOKEN_HERE" }
   ```
   You can include the `Bearer ` prefix or just the token.
3. Expected valid response includes `valid: true`, `subject`, and extracted claims (`sub`, `role`, `scope`, `iat`, `exp`).
4. Change one character in the token and submit again: expect HTTP 401.
5. Wait 15 minutes and validate the same token: expect HTTP 401 for expiry.
6. Try `GET /api/protected` with header `Authorization: Bearer PASTE_TOKEN_HERE`.

## Endpoints
- `POST /api/demo-token?subject=ramsai` — creates a short-lived demo JWT (testing only).
- `POST /api/validate` — validates signature and expiration, then extracts claims.
- `GET /api/protected` — demonstrates protecting a backend resource.

## Important security notes
- The demo-token endpoint is intentionally public for classroom testing. Remove it or restrict it before production.
- Replace `jwt.secret` with a secret supplied through an environment variable or secret manager. Never commit production secrets.
- JWT validation here uses HS256 shared-secret signing. The service that validates tokens must have the trusted signing secret; do not trust claims before signature verification.
- This example requires an `exp` claim and rejects tokens without one. Do not put passwords or other secrets in JWT claims.
- The `/api/protected` example validates the token in the controller for clarity. For production, integrate Spring Security's OAuth2 Resource Server support and configure issuer/audience as appropriate.
