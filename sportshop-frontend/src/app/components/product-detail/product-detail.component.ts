import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-product-detail',
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly productService = inject(ProductService);
  private readonly cartService = inject(CartService);
  private readonly keycloak = inject(KeycloakService);

  product = signal<Product | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.productService.getProductById(id).subscribe({
        next: (data) => {
          this.product.set(data);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Errore caricamento prodotto', err);
          this.loading.set(false);
        }
      });
    }
  }

  addToCart(p: Product) {
    // 1. CONTROLLA SE L'UTENTE È LOGGATO
    if (!this.keycloak.isLoggedIn()) {
      // Se non è loggato, reindirizza al login e poi torna qui
      this.keycloak.login({
        redirectUri: window.location.origin + '/products/' + p.id
      });
      return;
    }

    // 2. SE LOGGATO, PROCEDI CON L'AGGIUNTA AL CARRELLO
    this.cartService.addToCart(p.id, 1).subscribe({
      next: () => alert('Prodotto aggiunto al carrello!'),
      error: (err) => alert('Errore: ' + (err.error?.message || 'Impossibile aggiungere al carrello'))
    });
  }
}
