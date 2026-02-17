import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8082/prodotti';

  constructor(private http: HttpClient) { }

  // Ricerca dinamica con paginazione
  searchProducts(filters: any, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters.nome) params = params.set('nome', filters.nome);
    if (filters.marca) params = params.set('marca', filters.marca);
    if (filters.categoria) params = params.set('categoria', filters.categoria);
    if (filters.colore) params = params.set('colore', filters.colore);
    if (filters.taglia) params = params.set('taglia', filters.taglia);

    return this.http.get<any>(this.apiUrl, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  // Dati per i menu a tendina
  getMarche(): Observable<string[]> { return this.http.get<string[]>(`${this.apiUrl}/marche`); }
  getCategorie(): Observable<string[]> { return this.http.get<string[]>(`${this.apiUrl}/categorie`); }
  getColori(): Observable<string[]> { return this.http.get<string[]>(`${this.apiUrl}/colori`); }
  getTaglie(): Observable<string[]> { return this.http.get<string[]>(`${this.apiUrl}/taglie`); }
}
