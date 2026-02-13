import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';

export const authGuard: CanActivateFn = (route, state) => {
  const oauthService = inject(OAuthService);

  // Verifica se l'utente ha un token valido
  if (oauthService.hasValidAccessToken()) {
    return true;
  } else {
    // Se non è loggato, avvia il flusso di login e blocca la navigazione
    oauthService.initCodeFlow();
    return false;
  }
};
