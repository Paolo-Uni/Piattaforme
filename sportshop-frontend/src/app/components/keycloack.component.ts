import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-keycloack',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (!loggedIn) {
      <button (click)="login()" class="btn btn-primary">Login</button>
    } @else {
      <button (click)="logout()" class="btn btn-danger">Logout</button>
    }
  `
})
export class KeycloackComponent {

  loggedIn = false;

  constructor(private keycloak: KeycloakService) {
    // FIX: Nelle nuove versioni isLoggedIn() ritorna un booleano diretto, non una Promise.
    // Se ti da ancora errore, prova: this.keycloak.isLoggedIn().then(...)
    // Ma l'errore nello screenshot diceva "Property then does not exist on type boolean", quindi questa è la via giusta:
    this.loggedIn = this.keycloak.isLoggedIn();
  }

  login() {
    this.keycloak.login();
  }

  logout() {
    this.keycloak.logout();
  }
}
