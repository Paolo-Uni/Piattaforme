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

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (response) => {
        // response è l'oggetto Page di Spring Boot
        // response.content è l'array reale dei 10 prodotti
        this.products = response.content;
        console.log('Prodotti caricati:', this.products);
      },
      error: (err) => console.error('Errore nel caricamento prodotti', err)
    });
  }
  loadProducts() {
    this.productService.getProducts().subscribe(response => {
      // Estrai l'array 'content' dal formato Page di Spring Boot
      this.products = response.content;
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
