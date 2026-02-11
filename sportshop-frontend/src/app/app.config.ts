import { ApplicationConfig, provideZoneChangeDetection, APP_INITIALIZER, Provider } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { KeycloakService, KeycloakBearerInterceptor } from 'keycloak-angular';

// Funzione che inizializza Keycloak
function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8081',
        realm: 'myrealm',
        clientId: 'sport-client'
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false
      },
      // Nelle nuove versioni, l'interceptor si configura spesso separatamente,
      // ma lasciamo queste opzioni per compatibilità con il service
      enableBearerInterceptor: true,
      bearerExcludedUrls: ['/assets']
    });
}

// Configurazione manuale dei provider per evitare errori di tipo
const KeycloakProvider: Provider = {
  provide: APP_INITIALIZER,
  useFactory: initializeKeycloak,
  multi: true,
  deps: [KeycloakService]
};

const BearerInterceptorProvider: Provider = {
  provide: HTTP_INTERCEPTORS,
  useClass: KeycloakBearerInterceptor,
  multi: true
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Importante: withInterceptorsFromDi serve per far funzionare KeycloakBearerInterceptor (che è basato su classi)
    provideHttpClient(withInterceptorsFromDi()),
    KeycloakService,
    KeycloakProvider,
    BearerInterceptorProvider
  ]
};
