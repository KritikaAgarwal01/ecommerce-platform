---
title: Architecture Overview
description: Azure deployment and service architecture for the Java e-commerce platform
---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Azure Region: West US                            │
│                                                                         │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │              Azure Application Gateway (WAF v2)                   │  │
│  └─────────────────────────────┬─────────────────────────────────────┘  │
│                                │                                        │
│  ┌─────────────────────────────▼─────────────────────────────────────┐  │
│  │              Azure API Management (APIM)                          │  │
│  │   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │  │
│  │   │ Cart API │ │Payment   │ │ Order API│ │Reporting │           │  │
│  │   │ Policy   │ │API Policy│ │ Policy   │ │API Policy│           │  │
│  └───┼──────────┼─┼──────────┼─┼──────────┼─┼──────────┼───────────┘  │
│      │          │ │          │ │          │ │          │               │
│  ┌───▼──────────▼─▼──────────▼─▼──────────▼─▼──────────▼───────────┐  │
│  │                 Azure Kubernetes Service (AKS)                    │  │
│  │                                                                   │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │  │
│  │  │  Gateway │ │   Cart   │ │ Payment  │ │  Order   │           │  │
│  │  │ Service  │ │ Service  │ │ Service  │ │ Service  │           │  │
│  │  │ (Java)   │ │ (Java)   │ │ (Java)   │ │ (Java)   │           │  │
│  │  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘           │  │
│  │       │             │            │             │                  │  │
│  │  ┌────┴─────┐      │            │        ┌────┴─────┐           │  │
│  │  │Reporting │      │            │        │          │           │  │
│  │  │ Service  │      │            │        │          │           │  │
│  │  │ (Node)   │      │            │        │          │           │  │
│  │  └────┬─────┘      │            │        │          │           │  │
│  └───────┼─────────────┼────────────┼────────┼──────────┼───────────┘  │
│          │             │            │        │          │               │
│  ┌───────▼─────────────▼────────────┼────────┼──────────┼───────────┐  │
│  │         Azure Cosmos DB          │        │          │           │  │
│  │    (Cart, Reporting NoSQL)       │        │          │           │  │
│  └──────────────────────────────────┘        │          │           │  │
│                                              │          │           │  │
│  ┌───────────────────────────────────────────▼──────────▼───────────┐  │
│  │                    Azure SQL Server                               │  │
│  │          (Payment, Order Fulfillment, Reporting)                  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────┐  ┌──────────────────────┐                   │
│  │   Azure Key Vault    │  │  Azure Container     │                   │
│  │   (Secrets, Certs)   │  │  Registry (ACR)      │                   │
│  └──────────────────────┘  └──────────────────────┘                   │
└─────────────────────────────────────────────────────────────────────────┘
```

## Service Interactions

### Request Flow

1. External traffic enters through **Application Gateway** (WAF inspection).
2. Requests route to **APIM** for API governance (rate limiting, auth, transformation).
3. APIM forwards to the **Gateway Service** (BFF) or directly to backend services.
4. Backend services communicate internally via Kubernetes DNS (ClusterIP services).

### Service Communication Patterns

| Source | Target | Pattern | Protocol |
|--------|--------|---------|----------|
| Client → APIM | Gateway Service | Synchronous | HTTPS |
| Gateway → Cart | Cart Service | Synchronous | HTTP (internal) |
| Gateway → Payment | Payment Service | Synchronous | HTTP (internal) |
| Gateway → Order | Order Service | Synchronous | HTTP (internal) |
| Gateway → Reporting | Reporting Service | Synchronous | HTTP (internal) |
| Order → Payment | Payment Service | Synchronous | HTTP (internal) |
| Order → Cart | Cart Service | Synchronous | HTTP (internal) |
| Reporting → Cosmos DB | Data read | Direct query | SDK |
| Reporting → SQL Server | Data read | Direct query | Spring JDBC |

### Data Ownership

| Service | Primary Store | Access Pattern |
|---------|--------------|----------------|
| Cart Service | Cosmos DB (container: `carts`) | Read/Write — cart items per session/user |
| Payment Service | Azure SQL (`payments` database) | Read/Write — transactions, payment status |
| Order Service | Azure SQL (`orders` database) | Read/Write — order lifecycle, fulfillment state |
| Reporting Service | Cosmos DB + SQL (read replicas) | Read-only — cross-service aggregation |

### Security Model

- **External → APIM**: OAuth 2.0 / subscription keys enforced by APIM policies
- **APIM → AKS**: Mutual TLS or APIM-managed identity
- **Service → Database**: Managed Identity authentication (no connection strings in code)
- **Secrets**: Mounted via Azure Key Vault CSI driver into pods as volumes

### Extensibility for Multi-Region

The architecture supports future multi-region deployment through:

- Cosmos DB multi-region writes (account-level configuration change)
- Azure SQL geo-replication (failover groups)
- Azure Front Door replacing Application Gateway for global routing
- APIM multi-region gateway deployment
- AKS fleet management for cross-region orchestration

These extensions require no changes to service code — only infrastructure configuration updates.
