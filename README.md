# 🐙 GH-Challenge Issue Tracker CLI

A modular **Issue Tracking CLI** built with **Spring Boot**, **Picocli**, and **Java 21**.  

It supports two storage backends:
- **Local adapter** → In-memory repository (development & testing)
- **Google Sheets adapter** → Issues stored in a Google Sheet (persistence)

---

## 📂 Project Structure

### Modules
- **domain**  
  Core model (`Issue`, `Status`), service interfaces, and implementation (`IssueServiceImpl`). Pure business logic, no frameworks.

- **contracts**  
  DTOs (`IssueViewDTO`, `CreateIssueRequestDTO`, `UpdateStatusRequestDTO`) and enums used by CLI and facade generate by openApi.

- **app-facade**  
  Bridges domain and DTOs via `IssueFacade` and `IssueMapper` (MapStruct).

- **adapter-local**  
  In-memory repository (`InMemoryIssueRepository`), sequential ID generator, and system clock.  
  🔑 Activated with profile: `local`.

- **adapter-sheets**  
  Google Sheets integration (`GoogleSheetsIssueRepository`, `SheetsClientFactory`).  
  🔑 Activated with profile: `sheets`.

- **app-cli**  
  Entry point for the CLI. Provides commands:
  - `create` → Create a new issue  
  - `list` → List issues by status  
  - `update` → Update an issue’s status  

---

## 🛠️ Building

### With Maven
```bash
mvn clean package
```

This will build all modules and create a runnable JAR:  
`app-cli/target/app-cli-1.0.0.jar`

---

## 🚀 Running
### Before running the .env file needs to be filled:
SPREADSHEET_ID is in spredsheet link

SHEET_NAME is on the bottom of the excel
### Also the secrets/sa.json
paste there the secrets from google cloud console from service accounts/new key

### Local Profile (in-memory)
```bash
java -jar app-cli/target/app-cli-1.0.0.jar --spring.profiles.active=local
```

You’ll get an interactive CLI:

```
Interactive issue CLI. Type 'exit' to quit.
> create -d "Bug in login"
> list -s OPEN
> update -i AD-1 -s CLOSED
> exit
```

---

### Sheets Profile (Google Sheets backend)

**Requirements:**
- A Google Cloud service account JSON key
- A spreadsheet ID with a sheet named `issues`  
  (columns: `ID`, `Description`, `ParentId`, `Status`, `CreatedAt`, `UpdatedAt`)

Run:
```bash
java -jar app-cli/target/app-cli-1.0.0.jar \
  --spring.profiles.active=sheets \
  --sheets.spreadsheet-id=13TaPP3D8IewJoa8OwhmovHUP-P_8qMYa8nLqQTJZd14 \
  --sheets.credentials-path=secrets/sa.json \
  --sheets.sheet-name=issues
```

---

## 🐳 Running with Docker

### Build the image:
```bash
docker compose build
```

### Run with:
```bash
docker compose run --rm issues-cli
```
### Change profile with:
```
in .env change the sheets to local
```


## Commands
### List (status)
```bash
list -s OPEN
```
### Create (description, parent id)
```bash
create -d description -p AD-1
```
### Update (id)
```bash
update -i AD-1 -s CLOSED
```


## ✅ Testing & Coverage

Run unit + functional tests:
```bash
mvn clean verify
```
IntelliJ: *Run Tests with Coverage* to see line-level coverage.

---

## 🔑 Profiles

- `local` → In-memory adapter (development & testing)  
- `sheets` → Google Sheets adapter (production-like)  
- `test` → Disables interactive mode for automated tests  

---
