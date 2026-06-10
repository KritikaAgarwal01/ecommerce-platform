targetScope = 'subscription'

@description('Primary deployment region')
param location string = 'westus'

@description('Environment name')
@allowed(['dev', 'staging', 'prod'])
param environment string = 'dev'

@description('Project name prefix for resource naming')
param projectName string = 'ecom'

var resourceGroupName = 'rg-${projectName}-${environment}-${location}'
var tags = {
  project: projectName
  environment: environment
  managedBy: 'bicep'
}

resource resourceGroup 'Microsoft.Resources/resourceGroups@2024-03-01' = {
  name: resourceGroupName
  location: location
  tags: tags
}

module networking 'modules/networking.bicep' = {
  scope: resourceGroup
  name: 'networking-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
  }
}

module keyvault 'modules/keyvault.bicep' = {
  scope: resourceGroup
  name: 'keyvault-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
    subnetId: networking.outputs.privateEndpointSubnetId
  }
}

module cosmosdb 'modules/cosmosdb.bicep' = {
  scope: resourceGroup
  name: 'cosmosdb-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
    subnetId: networking.outputs.privateEndpointSubnetId
    vnetId: networking.outputs.vnetId
  }
}

module sqlserver 'modules/sqlserver.bicep' = {
  scope: resourceGroup
  name: 'sqlserver-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
    subnetId: networking.outputs.privateEndpointSubnetId
    vnetId: networking.outputs.vnetId
    keyVaultName: keyvault.outputs.keyVaultName
  }
}

module aks 'modules/aks.bicep' = {
  scope: resourceGroup
  name: 'aks-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
    subnetId: networking.outputs.aksSubnetId
    keyVaultName: keyvault.outputs.keyVaultName
  }
}

module appgateway 'modules/appgateway.bicep' = {
  scope: resourceGroup
  name: 'appgateway-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
    subnetId: networking.outputs.appGatewaySubnetId
  }
}

module apim 'modules/apim.bicep' = {
  scope: resourceGroup
  name: 'apim-deployment'
  params: {
    location: location
    projectName: projectName
    environment: environment
    tags: tags
    subnetId: networking.outputs.apimSubnetId
    appGatewayPublicIp: appgateway.outputs.publicIpAddress
  }
}

output resourceGroupName string = resourceGroup.name
output aksClusterName string = aks.outputs.clusterName
output apimGatewayUrl string = apim.outputs.gatewayUrl
output keyVaultName string = keyvault.outputs.keyVaultName
