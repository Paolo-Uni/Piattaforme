import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CartService } from '../../services/cart.service';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  filters = { nome: '', marca: '', categoria: '' };

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private keycloak: KeycloakService
  ) {}

  ngOnInit() { this.loadProducts(); }

  loadProducts() {
    this.productService.searchProducts(this.filters).subscribe({
      next: (res) => this.products = res.content,
      error: (err) => console.error(err)
    });
  }

  addToCart(p: Product) {
    this.cartService.addToCart(p.id, 1).subscribe({
      next: () => alert('Aggiunto al carrello!'),
      error: (err) => {
        if (err.status === 401) this.keycloak.login();
        else alert('Errore: ' + err.error?.message);
      }
    });
  }
}
