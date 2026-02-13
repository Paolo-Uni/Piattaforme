import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  // Assicurati che l'URL sia corretto (porta 8080)
  private apiUrl = 'http://localhost:8082/prodotti';

  constructor(private http: HttpClient) { }

  // --- GET PRODOTTI CON RICERCA DINAMICA ---
  searchProducts(filters: any, page: number = 0, size: number = 10, sortBy: string = 'nome'): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    // LOGICA DI PULIZIA: Aggiungi il parametro SOLO se ha un valore reale
    if (filters) {
      if (filters.nome && filters.nome.trim() !== '') params = params.set('nome', filters.nome);
      if (filters.marca && filters.marca !== 'Tutte' && filters.marca !== '') params = params.set('marca', filters.marca);
      if (filters.categoria && filters.categoria !== 'Tutte' && filters.categoria !== '') params = params.set('categoria', filters.categoria);
      if (filters.colore && filters.colore !== 'Tutti' && filters.colore !== '') params = params.set('colore', filters.colore);
      if (filters.taglia && filters.taglia !== 'Tutte' && filters.taglia !== '') params = params.set('taglia', filters.taglia);
    }

    console.log('Chiamata API Search con params:', params.toString()); // Debug
    return this.http.get<any>(`${this.apiUrl}/cerca`, { params });
  }

  // --- ALTRI METODI ---
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

  // Admin methods (lasciati invariati se non danno problemi)
  addProduct(product: Product): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/aggiungi`, product);
  }
  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/admin/elimina/${id}`);
  }
}
