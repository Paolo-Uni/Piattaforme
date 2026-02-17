import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartItem } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private apiUrl = 'http://localhost:8082/carrello';

  constructor(private http: HttpClient) { }

  getCart(): Observable<CartItem[]> {
    return this.http.get<CartItem[]>(this.apiUrl);
  }

  addToCart(idProdotto: number, quantita: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/aggiungi`, { idProdotto, quantita });
  }

  removeItem(idProdotto: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/rimuovi/${idProdotto}`);
  }

  increaseQuantity(idProdotto: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/incrementa/${idProdotto}`, {});
  }

  decreaseQuantity(idProdotto: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/decrementa/${idProdotto}`, {});
  }

  clearCart(): Observable<any> {
    return this.http.post(`${this.apiUrl}/svuota`, {});
  }

  checkout(indirizzoSpedizione: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/ordina`, { indirizzoSpedizione });
  }
}
