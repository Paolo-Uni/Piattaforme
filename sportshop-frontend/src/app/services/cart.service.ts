import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CartItem } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = 'http://localhost:8082/carrello';

  constructor(private http: HttpClient) {}

  addToCart(idProdotto: number, quantita: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/aggiungi?idProdotto=${idProdotto}&quantita=${quantita}`, {});
  }

  getCartItems(): Observable<CartItem[]> {
    return this.http.get<CartItem[]>(`${this.apiUrl}/items`);
  }

  incrementQuantity(idProdotto: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/plus?idProdotto=${idProdotto}`, {});
  }

  decrementQuantity(idProdotto: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/minus?idProdotto=${idProdotto}`, {});
  }

  checkout(indirizzo: string): Observable<any> {
    // Il backend CarrelloController.checkout probabilmente si aspetta l'indirizzo
    // Verifica se lo vuole come stringa semplice (body) o come oggetto JSON.
    // Esempio comune:
    return this.http.post(`${this.apiUrl}/checkout`, { indirizzoSpedizione: indirizzo });
  }
}
