import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductFilters, ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CartService } from '../../services/cart.service';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly cartService = inject(CartService);
  private readonly keycloak = inject(KeycloakService);

  // State management con Signals
  products = signal<Product[]>([]);

  // Filtri (binding con ngModel)
  filters: ProductFilters = { nome: '', marca: '', categoria: '' };

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.searchProducts(this.filters).subscribe({
      next: (response) => {
        // Assumiamo che response.content sia la lista, adattare se il BE risponde diversamente
        this.products.set(response.content);
      },
      error: (err) => console.error('Errore nel caricamento prodotti', err)
    });
  }

  addToCart(p: Product) {
    this.cartService.addToCart(p.id, 1).subscribe({
      next: () => alert('Aggiunto al carrello!'),
      error: (err) => {
        if (err.status === 401) this.keycloak.login();
        else alert('Errore: ' + (err.error?.message || 'Impossibile aggiungere al carrello'));
      }
    });
  }
}
