import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { isPlatformBrowser } from '@angular/common';
import { from, switchMap } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);
  const platformId = inject(PLATFORM_ID);

  // 1. Se siamo sul SERVER (SSR), passa la richiesta senza toccarla
  if (!isPlatformBrowser(platformId)) {
    return next(req);
  }

  // 2. Se siamo nel BROWSER ma l'utente non è loggato, passa senza token
  if (!keycloak.isLoggedIn()) {
    return next(req);
  }

  // 3. Utente loggato nel browser: aggiungi il token
  return from(keycloak.getToken()).pipe(
    switchMap(token => {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next(authReq);
    })
  );
};
