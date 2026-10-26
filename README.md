---
title: E-Commerce Platform
description: Java microservices reference architecture for an Azure-hosted e-commerce platform
---

## Overview

This production-oriented e-commerce reference platform uses Java 21, Spring Boot 3,
and a Maven multi-module build. It targets a single Azure region in West US.

## Architecture Overview

| Service | Technology | Database | Description |
|---------|-----------|----------|-------------|
| Cart Service | Spring Boot | Azure Cosmos DB | Shopping cart management |
| Payment Service | Spring Boot Data JPA | Azure SQL Server | Payment processing |
| Order Fulfillment | Spring Boot Data JPA | Azure SQL Server | Order lifecycle management |
| Reporting Service | Spring JDBC + Azure Cosmos SDK | Azure Cosmos DB + SQL | Operational and business reporting |
| Gateway Service | Spring Cloud Gateway | None | BFF edge routing behind APIM |

## Azure Platform Components

* Azure Kubernetes Service (AKS) for container orchestration
* Azure API Management (APIM) for API governance and exposure
* Azure Application Gateway and WAF for ingress protection
* Azure Key Vault and workload identity for secret access
* Azure Cosmos DB for NoSQL transactional workloads
* Azure SQL Server for relational workloads
* Azure Container Registry (ACR) for container images

## Repository Structure

```text
├── pom.xml                  Maven reactor for all services
├── docs/architecture/       Architecture documentation
├── infra/bicep/             Infrastructure as Code (Bicep)
├── helm/                    Kubernetes Helm charts
└── src/                     Java microservice modules
    ├── cart-service/
    ├── payment-service/
    ├── order-service/
    ├── reporting-service/
    └── gateway-service/
```

## Quick Start

### Prerequisites

* Java 21
* Maven 3.9+
* Azure CLI 2.50+
* Bicep CLI
* kubectl
* Helm 3.x
* Docker

### Build and test

Run the Maven reactor from the repository root:

```powershell
mvn clean verify
```

Each service can also be built independently with its required reactor projects:

```powershell
mvn -pl src/cart-service -am package
```

### Runtime configuration

The services use environment variables so the same artifacts work locally and in
AKS.

| Service | Required variables |
|---------|--------------------|
| Cart | `COSMOS_DB_ENDPOINT`; optional `COSMOS_DB_DATABASE`, `COSMOS_DB_CONTAINER` |
| Payment | `PAYMENTS_DB_URL`; optional `PAYMENTS_DB_USERNAME`, `PAYMENTS_DB_PASSWORD` |
| Order | `ORDERS_DB_URL`; optional database credentials and service URLs |
| Reporting | `SQL_CONNECTION_STRING`, `COSMOS_DB_ENDPOINT`; optional credentials and container settings |
| Gateway | Optional `CART_SERVICE_URL`, `PAYMENT_SERVICE_URL`, `ORDER_SERVICE_URL`, `REPORTING_SERVICE_URL` |

Cosmos clients use `DefaultAzureCredential`, which supports Azure workload identity
in AKS and developer credentials locally.

### Deploy Infrastructure

```powershell
az deployment sub create --location westus --template-file infra/bicep/main.bicep --parameters infra/bicep/parameters/westus.bicepparam
```

### Deploy Services

```powershell
helm upgrade --install cart-service helm/cart-service -n ecommerce
```

### Build container images

Each Dockerfile expects the repository root as its build context:

```powershell
docker build -f src/cart-service/Dockerfile -t cart-service:local .
docker build -f src/payment-service/Dockerfile -t payment-service:local .
docker build -f src/order-service/Dockerfile -t order-service:local .
docker build -f src/reporting-service/Dockerfile -t reporting-service:local .
docker build -f src/gateway-service/Dockerfile -t gateway-service:local .
```

## Design principles

* Single-region deployment in West US with extensibility for future multi-region
* Zero-trust networking with Key Vault secret injection through the CSI driver
* API-first governance through APIM policies
* Modular infrastructure with composable Bicep modules
* Helm-based Kubernetes deployments with environment-specific values

## License

MIT
