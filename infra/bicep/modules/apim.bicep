@description('Deployment location')
param location string

@description('Project name')
param projectName string

@description('Environment')
param environment string

@description('Resource tags')
param tags object

@description('APIM subnet ID')
param subnetId string

@description('App Gateway public IP for backend routing')
param appGatewayPublicIp string

var apimName = 'apim-${projectName}-${environment}'

resource apim 'Microsoft.ApiManagement/service@2023-09-01-preview' = {
  name: apimName
  location: location
  tags: tags
  sku: {
    name: 'Developer'
    capacity: 1
  }
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    publisherEmail: 'platform@contoso.com'
    publisherName: 'Contoso E-Commerce'
    virtualNetworkType: 'Internal'
    virtualNetworkConfiguration: {
      subnetResourceId: subnetId
    }
  }
}

resource cartApi 'Microsoft.ApiManagement/service/apis@2023-09-01-preview' = {
  parent: apim
  name: 'cart-api'
  properties: {
    displayName: 'Cart API'
    path: 'cart'
    protocols: ['https']
    serviceUrl: 'http://cart-service.ecommerce.svc.cluster.local'
    subscriptionRequired: true
    apiRevision: '1'
  }
}

resource paymentApi 'Microsoft.ApiManagement/service/apis@2023-09-01-preview' = {
  parent: apim
  name: 'payment-api'
  properties: {
    displayName: 'Payment API'
    path: 'payment'
    protocols: ['https']
    serviceUrl: 'http://payment-service.ecommerce.svc.cluster.local'
    subscriptionRequired: true
    apiRevision: '1'
  }
}

resource orderApi 'Microsoft.ApiManagement/service/apis@2023-09-01-preview' = {
  parent: apim
  name: 'order-api'
  properties: {
    displayName: 'Order API'
    path: 'order'
    protocols: ['https']
    serviceUrl: 'http://order-service.ecommerce.svc.cluster.local'
    subscriptionRequired: true
    apiRevision: '1'
  }
}

resource reportingApi 'Microsoft.ApiManagement/service/apis@2023-09-01-preview' = {
  parent: apim
  name: 'reporting-api'
  properties: {
    displayName: 'Reporting API'
    path: 'reporting'
    protocols: ['https']
    serviceUrl: 'http://reporting-service.ecommerce.svc.cluster.local'
    subscriptionRequired: true
    apiRevision: '1'
  }
}

// Global APIM policy for rate limiting and CORS
resource globalPolicy 'Microsoft.ApiManagement/service/policies@2023-09-01-preview' = {
  parent: apim
  name: 'policy'
  properties: {
    format: 'xml'
    value: '''
      <policies>
        <inbound>
          <cors allow-credentials="false">
            <allowed-origins><origin>*</origin></allowed-origins>
            <allowed-methods><method>GET</method><method>POST</method><method>PUT</method><method>DELETE</method></allowed-methods>
            <allowed-headers><header>*</header></allowed-headers>
          </cors>
          <rate-limit calls="100" renewal-period="60" />
          <base />
        </inbound>
        <backend><base /></backend>
        <outbound><base /></outbound>
        <on-error><base /></on-error>
      </policies>
    '''
  }
}

output apimName string = apim.name
output gatewayUrl string = apim.properties.gatewayUrl
output managementUrl string = apim.properties.managementApiUrl
