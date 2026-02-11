import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CartItem } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8082/carrello';

  addToCart(idProdotto: number, quantita: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/aggiungi`, {}, {
      params: { idProdotto, quantita }
    });
  }

  getCartItems(): Observable<CartItem[]> {
    return this.http.get<CartItem[]>(`${this.apiUrl}/items`);
  }

  incrementQuantity(idProdotto: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/plus`, {}, {
      params: { idProdotto }
    });
  }

  decrementQuantity(idProdotto: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/minus`, {}, {
      params: { idProdotto }
    });
  }

  checkout(indirizzo: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/checkout`, { indirizzoSpedizione: indirizzo });
  }
}
