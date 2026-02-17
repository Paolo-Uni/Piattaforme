import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router'; // Assicurati che ci siano questi
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive], // Devono essere qui
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  constructor(private oauthService: OAuthService) { }

  get isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  get username(): string {
    const claims: any = this.oauthService.getIdentityClaims();
    return claims ? (claims['given_name'] || claims['email']) : '';
  }

  login(): void {
    this.oauthService.initCodeFlow();
  }

  logout(): void {
    this.oauthService.logOut();
  }
}
