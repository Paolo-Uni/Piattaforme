import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, Page } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8082/prodotti';

  constructor(private http: HttpClient) { }

  // --- GET PRODOTTI CON RICERCA DINAMICA ---
  // Ritorna un Observable di Page<Product> perché il controller ritorna ResponseEntity<Page<Prodotto>>
  searchProducts(filters: any, page: number = 0, size: number = 10, sortBy: string = 'nome'): Observable<Page<Product>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    if (filters) {
      if (filters.nome?.trim()) params = params.set('nome', filters.nome);
      if (filters.marca && filters.marca !== 'Tutte') params = params.set('marca', filters.marca);
      if (filters.categoria && filters.categoria !== 'Tutte') params = params.set('categoria', filters.categoria);
      if (filters.colore && filters.colore !== 'Tutti') params = params.set('colore', filters.colore);
      if (filters.taglia && filters.taglia !== 'Tutte') params = params.set('taglia', filters.taglia);
    }

    return this.http.get<Page<Product>>(`${this.apiUrl}/cerca`, { params });
  }

  // --- ALTRI METODI ---
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/all`);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  getMarche(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/marche`);
  }

  getCategorie(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categorie`);
  }

  getColori(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/colori`);
  }

  getTaglie(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/taglie`);
  }

  // Admin methods
  addProduct(product: Product): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/aggiungi`, product);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/elimina/${id}`);
  }
}
