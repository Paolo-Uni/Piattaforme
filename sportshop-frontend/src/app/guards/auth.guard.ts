import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot
} from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override router: Router,
    protected override keycloakAngular: KeycloakService
  ) {
    super(router, keycloakAngular);
  }

  public async isAccessAllowed(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    // 1. Forza il login se l'utente non è autenticato
    if (!this.authenticated) {
      await this.keycloakAngular.login({
        redirectUri: window.location.origin + state.url
      });
      return false;
    }

    // 2. Controllo Ruoli (Opzionale: se la rotta richiede ruoli specifici come 'ADMIN')
    const requiredRoles = route.data['roles'];
    if (!(requiredRoles instanceof Array) || requiredRoles.length === 0) {
      return true; // Nessun ruolo richiesto, accesso consentito
    }

    // Controlla se l'utente ha i ruoli richiesti
    return requiredRoles.every((role) => this.roles.includes(role));
  }
}
