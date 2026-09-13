# Local Validation Record

Date: September 13, 2026  
Environment: Windows 11, Docker Desktop with WSL 2, Docker Compose, Git 2.55.0

This record captures the manual checks performed from a clean GitHub clone. Generated short codes and PostgreSQL data are intentionally local and are not committed.

## Results

| Check | Command or observation | Result |
|---|---|---|
| Docker engine | `docker run hello-world` | PASS — Docker returned `Hello from Docker!` |
| Clean checkout | `git clone https://github.com/karthikramravi/urlshortener.git` | PASS — 188 objects received and resolved |
| Build and automated tests | `docker compose up --build` | PASS — Maven `verify` completed during the image build |
| PostgreSQL startup | Compose database health check | PASS — application started after the database became healthy |
| Spring Boot startup | Container log | PASS — `Started UrlShortenerApplication` |
| Health endpoint | `curl.exe http://localhost:8080/actuator/health` | PASS — returned `{"status":"UP"}` |
| Create short URL | `POST /api/v1/urls` with `https://www.google.com` | PASS — returned a seven-character code and local short URL |
| Shorten a long URL | `POST /api/v1/urls` with a long Google search URL | PASS — returned a substantially shorter local URL |

## Reproduction commands

Run the application from the repository directory:

```powershell
docker compose up --build
```

In a second PowerShell window, check health:

```powershell
curl.exe http://localhost:8080/actuator/health
```

Create and display a short URL:

```powershell
$response = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/urls" -ContentType "application/json" -Body '{"originalUrl":"https://www.google.com/search?q=Spring+Boot+URL+Shortener+Java+Docker+PostgreSQL+example&sourceid=chrome&ie=UTF-8","expiresInDays":30}'
$response
```

Open the returned short URL:

```powershell
Start-Process $response.shortUrl
```

The repeatable automated evidence remains the authoritative quality gate. The latest `main` workflow runs `mvn -B clean verify`; detailed CI evidence is recorded in [CI-VALIDATION.md](CI-VALIDATION.md).
