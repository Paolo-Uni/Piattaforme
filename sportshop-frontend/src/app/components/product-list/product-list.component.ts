import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];

  marche: string[] = [];
  categorie: string[] = [];
  colori: string[] = [];
  taglie: string[] = [];

  // Inizializza con stringhe vuote. Il service ora gestisce la rimozione.
  filters: any = {
    nome: '',
    marca: '',
    categoria: '',
    colore: '',
    taglia: ''
  };

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadFilterOptions();
    this.search(); // Carica subito i prodotti
  }

  loadFilterOptions(): void {
    this.productService.getMarche().subscribe(data => this.marche = data);
    this.productService.getCategorie().subscribe(data => this.categorie = data);
    this.productService.getColori().subscribe(data => this.colori = data);
    this.productService.getTaglie().subscribe(data => this.taglie = data);
  }

  search(): void {
    this.productService.searchProducts(this.filters).subscribe({
      next: (data) => {
        // Gestisce sia la risposta impaginata (data.content) che lista semplice
        this.products = data.content ? data.content : data;
      },
      error: (err) => console.error('Errore nel caricamento prodotti:', err)
    });
  }

  resetFilters(): void {
    this.filters = { nome: '', marca: '', categoria: '', colore: '', taglia: '' };
    this.search();
  }
}
