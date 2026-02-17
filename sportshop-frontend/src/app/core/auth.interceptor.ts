import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Iniettiamo il servizio OAuth (assumendo tu stia usando angular-oauth2-oidc)
  const oauthService = inject(OAuthService);

  // Recuperiamo il token di accesso
  const token = oauthService.getAccessToken();

  // Se il token esiste, cloniamo la richiesta aggiungendo l'header
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
