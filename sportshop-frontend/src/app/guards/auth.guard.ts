import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

export const AuthGuard: CanActivateFn = async (route, state) => {
  const keycloak = inject(KeycloakService);
  const router = inject(Router);

  // Verifica se l'utente è loggato
  const loggedIn = keycloak.isLoggedIn();

  if (loggedIn) {
    // Controllo Ruoli (se definiti nella rotta)
    const requiredRoles = route.data['roles'] as string[];
    if (requiredRoles && requiredRoles.length > 0) {
      const hasRole = requiredRoles.some(role => keycloak.getUserRoles().includes(role));
      return hasRole;
    }
    return true;
  }

  // Se non è loggato, avvia il login
  await keycloak.login({
    redirectUri: window.location.origin + state.url
  });

  return false;
};
