import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './core/auth.config';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './components/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private oauthService: OAuthService) {
    this.configureAuth();
  }

  private configureAuth() {
    this.oauthService.configure(authConfig);

    // FIX FONDAMENTALE: Usa il localStorage invece del sessionStorage di default.
    // In questo modo il token sopravvive al refresh della pagina e alla chiusura della scheda.
    this.oauthService.setStorage(localStorage);

    this.oauthService.loadDiscoveryDocumentAndTryLogin()
      .then(() => {
        // Se l'utente ha un token ancora valido nel localStorage, avvia il timer per aggiornarlo in background
        if (this.oauthService.hasValidAccessToken()) {
          this.oauthService.setupAutomaticSilentRefresh();
          console.log('Sessione utente ripristinata dal localStorage.');
        } else {
          console.log('Nessuna sessione locale attiva.');
        }
      })
      .catch(err => {
        console.error('Errore Keycloak:', err);
      });
  }
}
