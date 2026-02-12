import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CartItem } from '../models/cart.model'; // Assicurati che il modello esista

@Injectable({ providedIn: 'root' })
export class CartService {
  // Assicurati che la porta corrisponda al tuo backend (8080 o 8082)
  private apiUrl = 'http://localhost:8082/carrello';

  constructor(private http: HttpClient) {}

  addToCart(idProdotto: number, quantita: number): Observable<any> {
    // FIX: Il backend usa @RequestParam, quindi usiamo HttpParams
    const params = new HttpParams()
      .set('idProdotto', idProdotto.toString())
      .set('quantita', quantita.toString());

    // Passiamo 'null' come body perché i dati sono nei params
    return this.http.post(`${this.apiUrl}/aggiungi`, null, { params });
  }

  getCartItems(): Observable<CartItem[]> {
    return this.http.get<CartItem[]>(`${this.apiUrl}/items`);
  }

  incrementQuantity(idProdotto: number): Observable<any> {
    const params = new HttpParams().set('idProdotto', idProdotto.toString());
    return this.http.put(`${this.apiUrl}/plus`, null, { params });
  }

  decrementQuantity(idProdotto: number): Observable<any> {
    const params = new HttpParams().set('idProdotto', idProdotto.toString());
    return this.http.put(`${this.apiUrl}/minus`, null, { params });
  }

  checkout(indirizzo: string): Observable<any> {
    const params = new HttpParams().set('indirizzoSpedizione', indirizzo);
    return this.http.post(`${this.apiUrl}/ordina`, null, { params });
  }
}
