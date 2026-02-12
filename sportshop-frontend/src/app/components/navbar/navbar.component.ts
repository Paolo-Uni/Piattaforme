import { Component, OnInit, signal, inject, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit {
  // Usiamo i signals per la reattività
  loggedIn = signal(false);
  username = signal('');
  isAdmin = signal(false);

  // Iniettiamo NgZone
  private ngZone = inject(NgZone);

  constructor(private keycloak: KeycloakService) {}

  async ngOnInit() {
    // 1. Verifica lo stato del login
    const isLoggedIn = this.keycloak.isLoggedIn();
    console.log('--- NAVBAR INIT ---');
    console.log('Is User Logged In?', isLoggedIn); // Guarda questo log nella console!

    if (isLoggedIn) {
      try {
        // 2. Carica il profilo utente
        const profile = await this.keycloak.loadUserProfile();
        console.log('Username:', profile.username);

        // 3. FORZA L'AGGIORNAMENTO GRAFICO CON NGZONE
        this.ngZone.run(() => {
          this.loggedIn.set(true);
          this.username.set(profile.username || '');
          this.isAdmin.set(this.keycloak.isUserInRole('ADMIN'));
        });

      } catch (error) {
        console.error('Errore caricamento profilo:', error);
      }
    } else {
      // Reset stato
      this.ngZone.run(() => {
        this.loggedIn.set(false);
        this.username.set('');
      });
    }
  }

  login() {
    this.keycloak.login();
  }

  register() {
    this.keycloak.register({
      redirectUri: window.location.origin
    });
  }

  logout() {
    this.keycloak.logout(window.location.origin);
  }
}
