import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  loggedIn = false;
  isAdmin = false;
  username = '';

  constructor(private keycloak: KeycloakService) {
    this.loggedIn = this.keycloak.isLoggedIn();
    if (this.loggedIn) {
      this.username = this.keycloak.getUsername();
      this.isAdmin = this.keycloak.getUserRoles().includes('ADMIN');
    }
  }

  login() { this.keycloak.login(); }
  logout() { this.keycloak.logout(); }
}
