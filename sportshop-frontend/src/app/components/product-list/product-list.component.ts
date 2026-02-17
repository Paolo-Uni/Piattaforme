import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Importante per *ngFor
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { FormsModule } from '@angular/forms'; // Per i filtri
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];

  // Filtri
  filters = {
    nome: '',
    marca: '',
    categoria: '',
    colore: '',
    taglia: ''
  };

  // Opzioni per le select
  marche: string[] = [];
  categorie: string[] = [];
  colori: string[] = [];
  taglie: string[] = [];

  // Paginazione
  page = 0;
  size = 10;
  totalPages = 0;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadFiltersData();
    this.search();
  }

  // Carica i dati per le select dei filtri
  loadFiltersData(): void {
    this.productService.getMarche().subscribe(data => this.marche = data);
    this.productService.getCategorie().subscribe(data => this.categorie = data);
    this.productService.getColori().subscribe(data => this.colori = data);
    this.productService.getTaglie().subscribe(data => this.taglie = data);
  }

  search(): void {
    // Reset paginazione a 0 quando si cerca
    this.page = 0;
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.searchProducts(this.filters, this.page, this.size).subscribe({
      next: (data) => {
        // CORREZIONE ERRORE: data è di tipo Page<Product>, i prodotti sono in data.content
        this.products = data.content;
        this.totalPages = data.totalPages;
        // Se la pagina corrente è maggiore del totale (es. dopo un filtro), resetta
        if (this.page >= this.totalPages && this.totalPages > 0) {
          this.page = 0;
          this.loadProducts(); // Ricarica con pagina 0
        }
      },
      error: (err) => {
        console.error('Errore caricamento prodotti', err);
      }
    });
  }

  onPageChange(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadProducts();
    }
  }

  resetFilters(): void {
    this.filters = {
      nome: '',
      marca: '',
      categoria: '',
      colore: '',
      taglia: ''
    };
    this.search();
  }
}
