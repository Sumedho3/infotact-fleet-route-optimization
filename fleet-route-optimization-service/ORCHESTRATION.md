# Multi-Container Deployment & Orchestration Blueprint

This guide details the infrastructure topology, environmental configuration matrix, and runtime operations for the containerized Fleet Route Optimization subsystem.

---

## 🌐 Infrastructure Architecture Topology

The application is orchestrated as a multi-container stack leveraging an isolated virtual network bridge (`fleet-network`) to ensure secure inter-container resolution.

1. **`fleet-optimization-service` (Java 21 / Spring Boot)**: Runs the core routing microservice engine, bound to host port `8081`.
2. **`mysql-database-server` (MySQL 8.0)**: Relational database instance mapping internal port `3306` out to host port `3307` for isolated development access.

---

## 📊 Environmental Variable Configuration Matrix

The deployment utilizes a combination of strict runtime variables and secure host system values passed dynamically at startup:

| Variable Name | Context / Target Purpose | Source / Type |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | Dictates active application properties (`docker`) | Hardcoded String |
| `DB_HOST` | Virtual network bridge alias (`mysql-container-host`) | Container Link Name |
| `DB_PORT` | Target database connection communication port (`3306`) | Internal Network Port |
| `DB_NAME` | Relational schema target locator name (`fleet_db`) | Database Name |
| `DB_USERNAME` | Master database root credentials user identifier (`root`) | Security Static |
| `DB_PASSWORD` | Linked user database key mapped from `${SYSTEM_DB_ROOT_PASSWORD}` | Host Environment |
| `MYSQL_PASSWORD` | App-tier database password authentication mapping | Host Environment |
| `MYSQL_ROOT_PASSWORD` | Superuser administrative database password mapping | Host Environment |
| `MAP_API_KEY` | Third-party geocoding routing engine credential access key | Host Environment |

---

## 🚀 Step-by-Step Deployment Operations

### 1. Verification of Host System Environment Variables
Before launching the stack, ensure your local terminal environment contains the required secure property seeds:
```bash
echo $SYSTEM_DB_ROOT_PASSWORD
echo $SYSTEM_MAP_API_KEY