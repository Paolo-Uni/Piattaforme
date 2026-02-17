import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { FormsModule } from '@angular/forms';
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

  filters = { nome: '', marca: '', categoria: '', colore: '', taglia: '' };

  marche: string[] = [];
  categorie: string[] = [];
  colori: string[] = [];
  taglie: string[] = [];

  page = 0;
  size = 12;
  totalPages = 0;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadFiltersData();
    this.loadProducts();
  }

  loadFiltersData(): void {
    this.productService.getMarche().subscribe(d => this.marche = d);
    this.productService.getCategorie().subscribe(d => this.categorie = d);
    this.productService.getColori().subscribe(d => this.colori = d);
    this.productService.getTaglie().subscribe(d => this.taglie = d);
  }

  search(): void {
    this.page = 0;
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.searchProducts(this.filters, this.page, this.size).subscribe({
      next: (data) => {
        this.products = data.content;
        this.totalPages = data.totalPages;
      },
      error: (err) => console.error(err)
    });
  }

  onPageChange(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadProducts();
      window.scrollTo(0, 0);
    }
  }

  resetFilters(): void {
    this.filters = { nome: '', marca: '', categoria: '', colore: '', taglia: '' };
    this.search();
  }
}
