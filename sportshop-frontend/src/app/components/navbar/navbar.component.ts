import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {
  private readonly keycloak = inject(KeycloakService);

  // Signals per lo stato
  loggedIn = signal(false);
  isAdmin = signal(false);
  username = signal('');

  async ngOnInit() {
    const isLogged = this.keycloak.isLoggedIn();
    this.loggedIn.set(isLogged);

    if (isLogged) {
      this.username.set(this.keycloak.getUsername());
      this.isAdmin.set(this.keycloak.getUserRoles().includes('ADMIN'));
    }
  }

  login() { this.keycloak.login(); }

  logout() {
    this.keycloak.logout(window.location.origin);
  }
}
