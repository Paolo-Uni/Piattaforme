import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import { KeycloakService } from 'keycloak-angular';
import { Product } from '../models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.component.html', // Assicurati che questo file esista, altrimenti usa template: `...`
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];
  filters = { nome: '', marca: '' };

  // Injection dei servizi nel costruttore
  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private keycloak: KeycloakService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    // Nota: Aggiunto any per bypassare controlli rigidi se il modello Page non è perfetto
    this.productService.searchProducts(this.filters).subscribe((response: any) => {
      this.products = response.content;
    });
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product.id, 1).subscribe({
      next: () => alert('Prodotto aggiunto!'),
      error: (err: any) => {
        if (err.status === 401) {
          this.keycloak.login(); // Se non loggato, manda al login
        } else {
          alert('Errore: ' + (err.error?.message || err.message));
        }
      }
    });
  }
}
