import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideOAuthClient, AuthConfig } from 'angular-oauth2-oidc';
import { authInterceptor } from './core/auth.interceptor';

const getRedirectUri = () => {
  if (typeof window !== 'undefined') {
    return window.location.origin + '/';
  }
  return 'http://localhost:4200/';
};

export const authConfig: AuthConfig = {
  issuer: 'http://localhost:8081/realms/myrealm', // URL Keycloak
  redirectUri: getRedirectUri(),
  clientId: 'sport-client',
  responseType: 'code',
  scope: 'openid profile email offline_access',
  showDebugInformation: true,
  requireHttps: false,
  sessionChecksEnabled: false
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor]) // Fondamentale per inviare il token
    ),
    provideOAuthClient({
      resourceServer: {
        // Le richieste verso questi URL avranno automaticamente il Token allegato
        allowedUrls: ['http://localhost:8082'],
        sendAccessToken: true
      }
    })
  ]
};
