import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark px-3">
      <a class="navbar-brand" routerLink="/">SportShop</a>
      <div class="collapse navbar-collapse">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item"><a class="nav-link" routerLink="/products">Prodotti</a></li>
          @if (loggedIn) {
            <li class="nav-item"><a class="nav-link" routerLink="/cart">Carrello</a></li>
            <li class="nav-item"><a class="nav-link" routerLink="/orders">I Miei Ordini</a></li>
            <li class="nav-item"><a class="nav-link" routerLink="/profile">Profilo</a></li>
          }
        </ul>
        <div class="d-flex">
          @if (!loggedIn) {
            <button class="btn btn-outline-light" (click)="login()">Login</button>
          } @else {
            <button class="btn btn-outline-danger" (click)="logout()">Logout</button>
          }
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  loggedIn = false;

  constructor(private keycloak: KeycloakService) {
    this.loggedIn = this.keycloak.isLoggedIn();
  }

  login() { this.keycloak.login(); }
  logout() { this.keycloak.logout(); }
}
