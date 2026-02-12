import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-product-detail',
  standalone: true, // Aggiunto per chiarezza, dato che usi imports
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
    // CORREZIONE: Usa subscribe invece di snapshot per garantire la lettura dell'ID
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      console.log('ID catturato dalla rotta:', idParam); // Debug

      if (idParam) {
        const id = Number(idParam);
        this.loadProduct(id);
      } else {
        console.error('Nessun ID trovato nella rotta');
        this.loading.set(false);
      }
    });
  }

  loadProduct(id: number) {
    this.loading.set(true);
    this.productService.getProductById(id).subscribe({
      next: (data) => {
        console.log('Prodotto scaricato:', data); // Debug
        this.product.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Errore caricamento prodotto:', err);
        // Se errore, product resta null e l'HTML mostrerà "Non trovato"
        this.loading.set(false);
      }
    });
  }

  addToCart(p: Product) {
    if (!this.keycloak.isLoggedIn()) {
      this.keycloak.login({
        redirectUri: window.location.origin + '/products/' + p.id
      });
      return;
    }

    this.cartService.addToCart(p.id, 1).subscribe({
      next: () => alert('Prodotto aggiunto al carrello!'),
      error: (err) => alert('Errore: ' + (err.error?.message || 'Impossibile aggiungere al carrello'))
    });
  }
}
