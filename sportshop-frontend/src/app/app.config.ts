import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideOAuthClient, AuthConfig } from 'angular-oauth2-oidc';
import { authInterceptor } from './core/auth.interceptor';

// Gestione sicura dell'URL per SSR
const getRedirectUri = () => {
  if (typeof window !== 'undefined') {
    return window.location.origin + '/';
  }
  return 'http://localhost:4200/';
};

export const authConfig: AuthConfig = {
  issuer: 'http://localhost:8081/realms/myrealm', // Assicurati che la porta e il realm siano giusti (8081 come hai detto)
  redirectUri: getRedirectUri(),
  clientId: 'sport-client',
  responseType: 'code',
  scope: 'openid profile email offline_access',
  showDebugInformation: true,
  requireHttps: false,
  // Fix per evitare errori di session storage su SSR
  sessionChecksEnabled: false
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    provideOAuthClient({
      resourceServer: {
        allowedUrls: ['http://localhost:8081'],
        sendAccessToken: true
      }
    })
  ]
};
