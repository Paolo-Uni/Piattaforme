import { Component, Inject, PLATFORM_ID, OnInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './app.config';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'sportshop-frontend';

  constructor(
    private oauthService: OAuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    // IL FIX FONDAMENTALE: Esegui la logica OAuth solo se siamo nel browser
    if (isPlatformBrowser(this.platformId)) {
      this.configureAuth();
    }
  }

  private configureAuth() {
    this.oauthService.configure(authConfig);

    // Carica la configurazione e prova il login
    this.oauthService.loadDiscoveryDocumentAndTryLogin()
      .then(() => {
        // Avvia il refresh automatico del token solo se il login ha avuto successo
        if (this.oauthService.hasValidAccessToken()) {
          this.oauthService.setupAutomaticSilentRefresh();
        }
      })
      .catch(err => console.error('Errore durante il login OAuth:', err));
  }
}
