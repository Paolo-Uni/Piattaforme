import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './core/auth.config';
import { CommonModule } from '@angular/common';
// IMPORTANTE: Importiamo la Navbar
import { NavbarComponent } from './components/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  // IMPORTANTE: Aggiungiamo NavbarComponent agli imports
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  // Colleghiamo il file HTML esterno invece di usare quello inline
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private oauthService: OAuthService) {
    this.configureAuth();
  }

  private configureAuth() {
    this.oauthService.configure(authConfig);

    // Gestione della Promise di login
    this.oauthService.loadDiscoveryDocumentAndTryLogin()
      .then(() => {
        this.oauthService.setupAutomaticSilentRefresh();
        console.log('Keycloak inizializzato.');
      })
      .catch(err => {
        console.error('Errore Keycloak:', err);
      });
  }
}
