import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  // URL del tuo Keycloak (da application.properties)
  issuer: 'http://localhost:8081/realms/myrealm',

  // URL del frontend dove l'utente viene reindirizzato dopo il login
  redirectUri: window.location.origin,

  // ID del Client su Keycloak (controlla che sia questo o 'sportshop-client')
  clientId: 'sport-client',

  // Permessi richiesti
  scope: 'openid profile email offline_access',

  // Importante per Keycloak moderno
  responseType: 'code',
  showDebugInformation: true,
  requireHttps: false // Solo per sviluppo locale
};
