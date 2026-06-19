# Fleet Management & Route Optimization
# Enterprise Fleet Management & Route Optimization Engine

## 🚛 Macroscopic Overview
This cloud-native microservice is designed to automate core fleet operations, manage driver-to-vehicle allocations, track asset maintenance states, and algorithmically optimize delivery routes to minimize distance, fuel burn, and turnaround time.

## 👥 Engineering Team & Domain Matrix
* *Sumedh (Core Architecture & Security)*: System scaffolding, database connection management, global exception handling, and API test slice verification.
* *Sreya (Fleet Asset Engineer)*: Driver and Vehicle object-relational mapping models, asset registry lifecycle services, and CRUD endpoints.
* *Soumya (Logistics Engine Planner)*: Delivery Task and Route state engines, tracking parameters, and core repository documentation.

## ⚙️ System Parameters & Technology Stack
* *Runtime Environment*: Java 17 / OpenJDK 21
* *Core Framework*: Spring Boot 3.x (Spring Web, Spring Data JPA)
* *Primary Database*: MySQL 8.x (ACID-compliant transaction logging)
* *Port Configuration*: 8081 (Preventing port collisions with Warehouse Management System instances)

## 🌿 Version Control Branching Blueprint
To comply with strict evaluation protocols, direct contributions to the main branch are forbidden. The repository follows a tiered git lifecycle:
1. main -> Production stable state; contains root repository structure.
2. dev -> Collaborative integration branch for sprint review.
3. feature/week1-fleet-schema -> Active isolated Week 1 development workspace.