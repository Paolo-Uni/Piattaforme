import { APP_INITIALIZER, ApplicationConfig, PLATFORM_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { KeycloakService } from 'keycloak-angular';
import { authInterceptor } from './core/auth.interceptor';
import { isPlatformBrowser } from '@angular/common';

// Funzione modificata per funzionare con SSR
function initializeKeycloak(keycloak: KeycloakService, platformId: Object) {
  return () => {
    // SE SIAMO SUL SERVER (SSR), NON FACCIAMO NULLA
    if (!isPlatformBrowser(platformId)) {
      return Promise.resolve();
    }

    // SE SIAMO SUL BROWSER, INIZIALIZZIAMO KEYCLOAK
    return keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'sportshop-realm',
        clientId: 'sportshop-client'
      },
      initOptions: {
        onLoad: 'check-sso',
        // Ora window è sicuro perché siamo dentro l'if (isPlatformBrowser)
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false
      },
      enableBearerInterceptor: true,
      bearerPrefix: 'Bearer',
    }).catch(error => {
      console.error('⚠️ Keycloak Init Failed:', error);
      return Promise.resolve(false);
    });
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    // MODIFICA: Aggiunto withFetch() per risolvere NG02801 e migliorare il supporto SSR
    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor])
    ),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      // INIETTIAMO IL PLATFORM_ID PER CAPIRE SE SIAMO SU BROWSER O SERVER
      deps: [KeycloakService, PLATFORM_ID]
    },
    KeycloakService
  ]
};
