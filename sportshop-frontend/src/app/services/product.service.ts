import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Page, Product } from '../models/product.model';

export interface ProductFilters {
  nome?: string;
  marca?: string;
  categoria?: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8082/prodotto';

  /**
   * Cerca prodotti con filtri e paginazione.
   */
  searchProducts(filters: ProductFilters, page: number = 0, size: number = 10): Observable<Page<Product>> {
    let params = new HttpParams()
      .set('pageNumber', page)
      .set('pageSize', size);

    if (filters.nome) params = params.set('nome', filters.nome);
    if (filters.marca) params = params.set('marca', filters.marca);
    if (filters.categoria) params = params.set('categoria', filters.categoria);

    return this.http.get<Page<Product>>(`${this.apiUrl}/search`, {params});
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  // ESEMPIO CORRETTO
  getProdotto(id: number): Observable<Product> {
    // Nota lo slash prima di ${id}
    return this.http.get<Product>(`${this.apiUrl}/prodotto/${id}`);
  }

  // Metodi ADMIN
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/create`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete`, {
      params: {idProdotto: id}
    });
  }

  addStock(id: number, quantita: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/add-stock`, {}, {
      params: {idProdotto: id, quantita: quantita}
    });
  }
}
