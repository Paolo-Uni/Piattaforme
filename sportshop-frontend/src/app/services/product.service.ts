import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page, Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = 'http://localhost:8082/prodotto'; // Verifica che la porta sia quella del backend

  constructor(private http: HttpClient) {}

  // Metodo per la ricerca paginata
  searchProducts(filters: any, page: number = 0, size: number = 10): Observable<Page<Product>> {
    let params = new HttpParams()
      .set('pageNumber', page)
      .set('pageSize', size);

    if (filters.nome) params = params.set('nome', filters.nome);
    if (filters.marca) params = params.set('marca', filters.marca);
    if (filters.categoria) params = params.set('categoria', filters.categoria);
    // Aggiungi altri filtri se necessario

    return this.http.get<Page<Product>>(`${this.apiUrl}/search`, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`); // Supponendo che tu abbia ripristinato l'endpoint
  }
}
