import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router'; // AGGIUNTO ActivatedRoute
import { FormsModule } from '@angular/forms';
import { ProductFilters, ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { CartService } from '../../services/cart.service';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly cartService = inject(CartService);
  private readonly keycloak = inject(KeycloakService);
  private readonly route = inject(ActivatedRoute); // INIEZIONE ROUTE

  products = signal<Product[]>([]);

  // Liste per i menu a tendina
  marcheList = signal<string[]>([]);
  categorieList = signal<string[]>([]);
  taglieList = signal<string[]>([]);
  coloriList = signal<string[]>([]);

  filters: ProductFilters = { nome: '', marca: '', categoria: '', colore: '', taglia: '' };

  ngOnInit(): void {
    // 1. Carica le opzioni per le tendine
    this.loadFilters();

    // 2. Ascolta i parametri dell'URL (es. ?marca=Nike dalla Home)
    this.route.queryParams.subscribe(params => {
      // Se c'è un parametro nell'URL, aggiorna il filtro, altrimenti usa stringa vuota
      this.filters.marca = params['marca'] || '';
      this.filters.categoria = params['categoria'] || '';

      // Nota: Angular aggiornerà automaticamente il menu a tendina (select)
      // perché è legato con [(ngModel)]="filters.marca"

      // 3. Carica i prodotti con i filtri applicati
      this.loadProducts();
    });
  }

  loadFilters() {
    this.productService.getMarche().subscribe(data => this.marcheList.set(data));
    this.productService.getCategorie().subscribe(data => this.categorieList.set(data));
    this.productService.getTaglie().subscribe(data => this.taglieList.set(data));
    this.productService.getColori().subscribe(data => this.coloriList.set(data));
  }

  loadProducts() {
    const activeFilters = { ...this.filters };

    this.productService.searchProducts(activeFilters).subscribe({
      next: (response) => {
        this.products.set(response.content);
      },
      error: (err) => console.error('Errore nel caricamento prodotti', err)
    });
  }

  addToCart(p: Product) {
    if (!this.keycloak.isLoggedIn()) {
      this.keycloak.login();
      return;
    }

    this.cartService.addToCart(p.id, 1).subscribe({
      next: () => alert('Aggiunto al carrello!'),
      error: (err) => alert('Errore: ' + (err.error?.message || 'Impossibile aggiungere'))
    });
  }
}
