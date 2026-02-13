import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  constructor(private oauthService: OAuthService) { }

  // Getter per verificare se l'utente è loggato
  get isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  // Getter per ottenere il nome (opzionale, dal token)
  get userName(): string {
    const claims: any = this.oauthService.getIdentityClaims();
    return claims ? claims['given_name'] : 'Utente';
  }

  // Getter per verificare se è admin (controlla i ruoli nel token)
  get isAdmin(): boolean {
    const token = this.oauthService.getAccessToken();
    if(!token) return false;
    // Decodifica semplice del JWT per cercare realm_access.roles
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.realm_access?.roles?.includes('admin');
    } catch(e) {
      return false;
    }
  }

  login(): void {
    this.oauthService.initCodeFlow();
  }

  logout(): void {
    this.oauthService.logOut();
  }
}
