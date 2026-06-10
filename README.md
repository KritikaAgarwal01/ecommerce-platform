# E-Commerce Platform — Microservices Reference Architecture

A production-oriented reference e-commerce platform built on microservices architecture, targeting Azure (West US, single-region deployment).

## Architecture Overview

| Service | Technology | Database | Description |
|---------|-----------|----------|-------------|
| Cart Service | Node.js / TypeScript | Azure Cosmos DB | Shopping cart management |
| Payment Service | .NET 8 / ASP.NET Core | Azure SQL Server | Payment processing |
| Order Fulfillment | .NET 8 / ASP.NET Core | Azure SQL Server | Order lifecycle management |
| Reporting Service | Node.js / TypeScript | Azure Cosmos DB + SQL | Operational and business reporting |
| Gateway Service | Node.js / TypeScript | — | BFF edge routing behind APIM |

## Azure Platform Components

- **Azure Kubernetes Service (AKS)** — Container orchestration
- **Azure API Management (APIM)** — API governance and exposure
- **Azure Application Gateway + WAF** — Ingress and web application firewall
- **Azure Key Vault** — Secrets and configuration management
- **Azure Cosmos DB** — NoSQL transactional workloads
- **Azure SQL Server** — Relational workloads
- **Azure Container Registry (ACR)** — Container image storage

## Repository Structure

```
├── docs/architecture/       Architecture documentation
├── infra/bicep/             Infrastructure as Code (Bicep)
├── helm/                    Kubernetes Helm charts
│   ├── cart-service/
│   ├── payment-service/
│   ├── order-service/
│   ├── reporting-service/
│   └── gateway-service/
└── src/                     Microservice source code
    ├── cart-service/        Node.js
    ├── payment-service/     .NET
    ├── order-service/       .NET
    ├── reporting-service/   Node.js
    └── gateway-service/     Node.js
```

## Quick Start

### Prerequisites

- Azure CLI 2.50+
- Bicep CLI
- kubectl
- Helm 3.x
- Node.js 20 LTS
- .NET 8 SDK
- Docker

### Deploy Infrastructure

```bash
az deployment sub create \
  --location westus \
  --template-file infra/bicep/main.bicep \
  --parameters infra/bicep/parameters/westus.bicepparam
```

### Deploy Services

```bash
helm upgrade --install cart-service helm/cart-service -n ecommerce
helm upgrade --install payment-service helm/payment-service -n ecommerce
helm upgrade --install order-service helm/order-service -n ecommerce
helm upgrade --install reporting-service helm/reporting-service -n ecommerce
helm upgrade --install gateway-service helm/gateway-service -n ecommerce
```

## Design Principles

- **Single-region** deployment (West US) with extensibility for future multi-region
- **Zero-trust networking** with Key Vault secret injection via CSI driver
- **API-first** governance through APIM policies
- **Modular IaC** with composable Bicep modules
- **Helm-based** Kubernetes deployments with environment-specific values

## License

MIT
