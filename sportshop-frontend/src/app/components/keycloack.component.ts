import { Component, OnInit } from '@angular/core';
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
      <div class="d-flex align-items-center gap-2">
        <span class="text-white me-2">Ciao, {{ username }}</span>
        <button (click)="logout()" class="btn btn-danger btn-sm">Logout</button>
      </div>
    }
  `
})
export class KeycloackComponent implements OnInit {
  loggedIn = false;
  username = '';

  constructor(private keycloak: KeycloakService) {}

  async ngOnInit() {
    // isLoggedIn() ritorna un booleano sincrono nelle versioni recenti,
    // ma per sicurezza lo avvolgiamo in un try/catch se ci sono problemi di inizializzazione
    try {
      this.loggedIn = this.keycloak.isLoggedIn();
      if (this.loggedIn) {
        const profile = await this.keycloak.loadUserProfile();
        this.username = profile.username || '';
      }
    } catch (e) {
      console.error('Errore lettura stato Keycloak:', e);
    }
  }

  login() {
    this.keycloak.login();
  }

  logout() {
    this.keycloak.logout();
  }
}
