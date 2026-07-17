# Fleet Route Optimization Service

A high-throughput backend microservice designed within the Java ecosystem to calculate geographic distance matrices, optimize fleet dispatches, and process live telematics tracking profiles.

---

## 🏗️ Core Architecture Overview

This microservice handles algorithmic optimization pipelines using a decoupled layer topology:
* **Controller Layer**: Exposes endpoints and manages interactive REST validation parameters.
* **Service Layer**: Orchestrates business constraints and manages external OSRM mapping streams.
* **Data Access Layer**: Persists route properties via Spring Data JPA into the relational tier.

```text
                  ┌───────────────────────────────────────────────┐
                  │                 Web Browser                   │
                  │         (Swagger UI / API Consumers)          │
                  └───────────────────────┬───────────────────────┘
                                          │  (Port 8081)
                                          ▼
                  ┌───────────────────────────────────────────────┐
                  │      Fleet Route Optimization Service         │
                  │            (Spring Boot Engine)               │
                  └───────┬───────────────────────────────┬───────┘
                          │                               │
       (Dev Profile Profile Port 3307)       (Docker Profile Host Mesh)
                          ▼                               ▼
     ┌─────────────────────────────────────────┐   ┌───────────────────────────┐
     │      Containerized Target MySQL         │   │  Virtual Internal Network │
     │        (Host Port Mapping: 3307)        │   │     (mysql-container-host)│
     └─────────────────────────────────────────┘   └───────────────────────────┘


```

---

## ⚙️ Deployment & Profile Configurations

The repository features isolated execution profiles to completely segregate developer and container environments:

### 🚀 Target Profile Selection Matrix

| Execution Environment | Active Profile | Target Configuration Asset | Target Port Matrix |
| :--- | :---: | :--- | :--- |
| **Native / Hybrid Local IDE** | `dev` | `src/main/resources/application-dev.yml` | **Web UI**: `8081`<br>**Database**: `localhost:3307` |
| **Multi-Container Orchestration** | `docker` | `src/main/resources/application-docker.yml` | **Command**: `docker-compose up --build` |

---

## 🛡️ Exception & Validation Boundaries

> 💡 **Architectural Note:** The service layer maintains strict exception boundaries using an asynchronous reflection pipeline to trap container runtime data faults.

* **Centralized Interceptor:** Utilizes a customized `@RestControllerAdvice` layer to trap runtime operational failures globally.
* **Standardized Payload:** Wraps runtime failures cleanly within a deterministic JSON matrix containing precise error details, timestamp offsets, and localized API routing context paths.
* **Dependency Stability:** Uses aligned reflection parsing dependencies to natively bypass library signature conflicts under modern Spring Boot parent architectures.

---

## 🧪 Verification & Testing Procedures

### 🧩 Boot Verification Trace
Verify that the application initializes cleanly under the proper target profile by validating early log statements:


The following 1 profile is active: "dev"

