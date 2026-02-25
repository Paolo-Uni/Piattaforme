import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './core/auth.config';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './components/navbar/navbar.component';
// 1. Importa il UserService
import { UserService } from './services/user.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(
    private oauthService: OAuthService,
    private userService: UserService // 2. Inietta il UserService
  ) {
    this.configureAuth();
  }

  private configureAuth() {
    this.oauthService.configure(authConfig);

    // FIX FONDAMENTALE: Usa il localStorage invece del sessionStorage di default.
    this.oauthService.setStorage(localStorage);

    this.oauthService.loadDiscoveryDocumentAndTryLogin()
      .then(() => {
        // Se l'utente ha un token ancora valido...
        if (this.oauthService.hasValidAccessToken()) {
          this.oauthService.setupAutomaticSilentRefresh();
          console.log('Sessione utente ripristinata dal localStorage.');

          // 3. LA MAGIA AVVIENE QUI:
          // Chiamiamo il backend immediatamente dopo il login per forzare la sincronizzazione nel DB
          this.userService.getMe().subscribe({
            next: (user) => console.log('Utente sincronizzato con successo nel DB Spring Boot:', user),
            error: (err) => console.error('Errore durante la sincronizzazione col DB:', err)
          });

        } else {
          console.log('Nessuna sessione locale attiva.');
        }
      })
      .catch(err => {
        console.error('Errore Keycloak:', err);
      });
  }
}
