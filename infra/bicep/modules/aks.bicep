@description('Deployment location')
param location string

@description('Project name')
param projectName string

@description('Environment')
param environment string

@description('Resource tags')
param tags object

@description('AKS subnet ID')
param subnetId string

@description('Key Vault name for storing cluster credentials')
param keyVaultName string

var clusterName = 'aks-${projectName}-${environment}'
var nodeResourceGroup = 'rg-${projectName}-${environment}-aks-nodes'
var acrName = replace('acr${projectName}${environment}', '-', '')

resource acr 'Microsoft.ContainerRegistry/registries@2023-11-01-preview' = {
  name: acrName
  location: location
  tags: tags
  sku: {
    name: 'Standard'
  }
  properties: {
    adminUserEnabled: false
  }
}

resource aks 'Microsoft.ContainerService/managedClusters@2024-02-01' = {
  name: clusterName
  location: location
  tags: tags
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    dnsPrefix: '${projectName}-${environment}'
    nodeResourceGroup: nodeResourceGroup
    networkProfile: {
      networkPlugin: 'azure'
      networkPolicy: 'calico'
      serviceCidr: '10.1.0.0/16'
      dnsServiceIP: '10.1.0.10'
    }
    agentPoolProfiles: [
      {
        name: 'system'
        count: 2
        vmSize: 'Standard_D4s_v5'
        mode: 'System'
        osType: 'Linux'
        osSKU: 'AzureLinux'
        vnetSubnetID: subnetId
        enableAutoScaling: true
        minCount: 2
        maxCount: 5
      }
      {
        name: 'workload'
        count: 3
        vmSize: 'Standard_D4s_v5'
        mode: 'User'
        osType: 'Linux'
        osSKU: 'AzureLinux'
        vnetSubnetID: subnetId
        enableAutoScaling: true
        minCount: 3
        maxCount: 10
        nodeTaints: []
        nodeLabels: {
          workload: 'ecommerce'
        }
      }
    ]
    addonProfiles: {
      azureKeyvaultSecretsProvider: {
        enabled: true
        config: {
          enableSecretRotation: 'true'
          rotationPollInterval: '2m'
        }
      }
    }
    oidcIssuerProfile: {
      enabled: true
    }
    securityProfile: {
      workloadIdentity: {
        enabled: true
      }
    }
  }
}

// Grant AKS pull access to ACR
resource acrPullRole 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(acr.id, aks.id, 'acrpull')
  scope: acr
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', '7f951dda-4ed3-4680-a7ca-43fe172d538d')
    principalId: aks.properties.identityProfile.kubeletidentity.objectId
    principalType: 'ServicePrincipal'
  }
}

output clusterName string = aks.name
output clusterFqdn string = aks.properties.fqdn
output acrName string = acr.name
output acrLoginServer string = acr.properties.loginServer
output kubeletIdentityObjectId string = aks.properties.identityProfile.kubeletidentity.objectId
