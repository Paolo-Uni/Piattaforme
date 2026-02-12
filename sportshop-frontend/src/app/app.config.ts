import { APP_INITIALIZER, ApplicationConfig, PLATFORM_ID, NgZone } from '@angular/core';
import { provideRouter, withComponentInputBinding, withViewTransitions } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { KeycloakService } from 'keycloak-angular';
import { isPlatformBrowser } from '@angular/common';
import { authInterceptor } from './core/auth.interceptor';

// Funzione di inizializzazione
export function initializeKeycloak(keycloak: KeycloakService, platformId: Object) {
  return () => {
    if (!isPlatformBrowser(platformId)) {
      return Promise.resolve(true);
    }

    return keycloak.init({
      config: {
        url: 'http://localhost:8081',
        realm: 'myrealm',
        clientId: 'sport-client'
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false, // Importante per la velocità
        enableLogging: true
      },
      enableBearerInterceptor: false,
      bearerPrefix: 'Bearer',
    }).catch(err => console.error('Keycloak init error:', err));
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    // Rimuovi provideClientHydration() se c'era!
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideAnimations(),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService, PLATFORM_ID]
    }
  ]
};
