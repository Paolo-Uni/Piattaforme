import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page, Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = 'http://localhost:8082/prodotto';

  constructor(private http: HttpClient) {}

  searchProducts(filters: any, page: number = 0, size: number = 10): Observable<Page<Product>> {
    let params = new HttpParams().set('pageNumber', page).set('pageSize', size);
    if (filters.nome) params = params.set('nome', filters.nome);
    if (filters.marca) params = params.set('marca', filters.marca);
    if (filters.categoria) params = params.set('categoria', filters.categoria);
    return this.http.get<Page<Product>>(`${this.apiUrl}/search`, { params });
  }

  // Metodi aggiunti per completezza
  getProductById(id: number): Observable<Product> {
    // Nota: Ho visto nel backend che hai commentato/riaggiunto questo endpoint. Assicurati che ci sia in ProdottoService.java!
    // Se non c'è, Angular darà errore 404 o 405.
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  // Metodi ADMIN
  createProduct(product: Product): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete?idProdotto=${id}`);
  }

  addStock(id: number, quantita: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/add-stock?idProdotto=${id}&quantita=${quantita}`, {});
  }
}
