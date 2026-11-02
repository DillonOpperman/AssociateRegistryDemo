# Associate Registry Demo

A locally hosted associate-management application for demonstrating enterprise web development in a software engineering interview. It is a personal portfolio project. It is not an official employer system, it does not use any company branding, and every person in the database is fictional.

Administrators and read-only users are simulated in the browser. That role selector is a user-interface demonstration. It is not authentication, and it is not secure access control.

## Instructions

### What you need

- Windows with PowerShell
- A JDK 21 or newer (`java` on `PATH`, or `JAVA_HOME` set)
- Node.js 22.12 or newer and npm
- Internet access the first time, so Maven and npm can download dependencies

This project was built with Java 25 and Node.js 22.13. Maven does not need to be installed separately. `backend/mvnw.cmd` downloads it.

### Run it

1. Clone the repository and open PowerShell in that folder.

```powershell
git clone https://github.com/DillonOpperman/AssociateRegistryDemo.git
cd AssociateRegistryDemo
```

2. Start the API and the UI. This opens two PowerShell windows. The first API start can take a minute while Maven finishes. If `frontend/node_modules` is missing, the UI window runs `npm install` first.

```powershell
.\start.ps1
```

3. Wait until the API window prints `Started AssociateRegistryApplication`.

4. Open the app at http://localhost:4200. The API is at http://localhost:8080.

5. Stop the app by pressing `Ctrl+C` in each window.

### Try the demo

- Leave the role set to **Administrator** to register an associate or edit a profile.
- Switch the role in the top bar to **Read-Only User**. Register and edit controls disappear. Those routes send you back to the directory.
- The banner on every page says these roles are simulated. The API does not enforce them.
- The directory starts with 32 fictional associates. Search, department, and status filters all come from the API.
- Changes you save stay in `backend/data` after you restart. That folder is not part of the git repository. Delete it and restart the API to load the original sample data again.

### Start each app yourself

Use this when you want the API and the UI in terminals you already have open.

```powershell
$env:JAVA_HOME = "C:\Program Files\Semeru\jdk-25.0.2.10-openj9"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

# Terminal 1 — API
cd .\backend
.\mvnw.cmd spring-boot:run

# Terminal 2 — UI
cd .\frontend
npm install
npm start
```

Change `JAVA_HOME` if your JDK is installed somewhere else. `npm install` is only needed the first time, or after dependencies change.

### Check that it builds

```powershell
cd .\backend
.\mvnw.cmd test

cd ..\frontend
npm run build
```

## Architecture

The repository has two applications:

```text
AssociateRegistryDemo/
├── frontend/    Angular SPA
├── backend/     Spring Boot REST API and H2 database
├── start.ps1    Opens both applications
└── README.md
```

The Angular app calls `/api` on the same origin. During development, the Angular dev server proxies those requests to `http://localhost:8080`. The API also allows browser calls from `http://localhost:4200` directly.

```text
Browser  →  Angular (port 4200)  →  /api proxy  →  Spring Boot (port 8080)  →  H2 file database
```

Backend packages:

- `controller` — REST endpoints
- `service` — registration, updates, search, dashboard totals, and audit recording
- `repository` — Spring Data JPA
- `entity` — associate and audit-log tables
- `dto` — request and response records
- `exception` — not-found, duplicate, and centralized API errors
- `configuration` — CORS and the one-time sample-data loader

Frontend folders:

- `pages` — dashboard, directory, profile, registration, editing, and audit log
- `components` — application shell and the shared associate form
- `services` — HTTP calls and the simulated role
- `models` — TypeScript types for API payloads
- `shared` — route guard, demo-user header, status badge, and formatting

## Technologies

| Area | Choice |
| --- | --- |
| UI | Angular 21.2, TypeScript, Angular Router, Reactive Forms, HttpClient, Angular Material |
| API | Java 21 language level, Spring Boot 4.1.2, Spring Web MVC, Spring Data JPA, Jakarta Bean Validation |
| Database | H2 2.4, file-based, stored in `backend/data` |
| Build | Maven Wrapper, npm |

The backend is compiled with `--release 21` and runs on Java 21 through 27, including Java 25. Maven does not need to be on `PATH`; `backend/mvnw.cmd` downloads and runs it. Angular 22 needs a newer Node.js than 22.13, so the UI uses Angular 21, which supports the Node version listed above.

## Sample data

The API inserts 32 fictional associates, plus creation and update audit rows, only when the associate table is empty. Restarting the API does not duplicate them. The H2 files live in `backend/data/` and are ignored by git.

H2 console (local only): http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:file:./data/associate-registry`
- User: `sa`
- Password: empty

The JDBC URL is relative to the backend working directory.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/api/associates` | Search, filter, sort, and page associates |
| `GET` | `/api/associates/{id}` | One associate by database id |
| `POST` | `/api/associates` | Register an associate |
| `PUT` | `/api/associates/{id}` | Update an associate |
| `GET` | `/api/associates/departments` | Distinct departments for the filter |
| `GET` | `/api/dashboard/stats` | Counts, recent changes, and recent registrations |
| `GET` | `/api/audit-logs` | Paged audit history |

`GET /api/associates` accepts `search`, `department`, `status`, `page`, `size`, and `sort`. Search matches first name, last name, full name, email, and associate ID. `status` is `ACTIVE`, `INACTIVE`, or `ON_LEAVE`. `sort` looks like `lastName,asc`. Page size is capped at 100.

Create and update send `X-Demo-User`. The UI sets that header from the simulated role. If the header is missing, the audit row records `Demo User`.

Example:

```powershell
Invoke-RestMethod http://localhost:8080/api/dashboard/stats
```

Validation failures return HTTP 400 with `fieldErrors`. Unknown associates return 404. A duplicate associate ID or email returns 409.

## Features

- Dashboard counts and activity come from the database.
- Directory with search, department filter, status filter, sorting, and pagination.
- Profile, registration, and editing. Registration uses reactive forms and shows field errors.
- Status values Active, Inactive, and On Leave, shown as badges.
- Backend audit log for creates and updates, including field changes and the demo actor.
- Simulated Administrator and Read-Only User roles. Read-only users do not see register or edit controls, and those routes redirect them to the directory.

## Known limitations

- The role selector is not security. The API does not authenticate callers or reject updates from a read-only demo user.
- H2 is for the local demo. It is not a production database.
- There is no delete action, password login, or single sign-on.
- Sample people, phone numbers, and `@example.com` addresses are synthetic.
- Angular Material still uses the Angular animations package, which Angular 21 marks as deprecated. The UI does not add its own animation.
