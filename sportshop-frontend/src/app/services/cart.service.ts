import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartItem } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private apiUrl = 'http://localhost:8082/carrello';

  constructor(private http: HttpClient) { }

  addToCart(idProdotto: number, quantita: number): Observable<any> {
    const params = new HttpParams()
      .set('idProdotto', idProdotto.toString())
      .set('quantita', quantita.toString());

    return this.http.post(`${this.apiUrl}/aggiungi`, null, { params });
  }

  getCart(): Observable<CartItem[]> {
    return this.http.get<CartItem[]>(`${this.apiUrl}/vedi`);
  }

  // Decrementa quantità
  decreaseQuantity(idProdotto: number): Observable<any> {
    const params = new HttpParams().set('idProdotto', idProdotto.toString());
    return this.http.post(`${this.apiUrl}/rimuovi`, null, { params });
  }

  // Incrementa quantità
  increaseQuantity(idProdotto: number): Observable<any> {
    const params = new HttpParams().set('idProdotto', idProdotto.toString());
    return this.http.post(`${this.apiUrl}/aumenta`, null, { params });
  }

  // Rimuove totalmente il prodotto (corrisponde a @DeleteMapping("/elimina-prodotto") con @RequestParam)
  removeItem(idProdotto: number): Observable<any> {
    const params = new HttpParams().set('idProdotto', idProdotto.toString());
    return this.http.delete(`${this.apiUrl}/elimina-prodotto`, { params });
  }

  clearCart(): Observable<any> {
    return this.http.post(`${this.apiUrl}/svuota`, {});
  }

  // Checkout: invia mappa con indirizzo come richiesto da @RequestBody Map<String, String> body
  checkout(indirizzoSpedizione: string): Observable<any> {
    const body = { indirizzoSpedizione: indirizzoSpedizione };
    return this.http.post(`${this.apiUrl}/ordina`, body);
  }
}
