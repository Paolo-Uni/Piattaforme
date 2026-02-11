import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = 'http://localhost:8082/ordine';

  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/miei-ordini`);
  }

  cancelOrder(idOrdine: number, motivo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${idOrdine}/annulla?motivo=${motivo}`, {});
  }
}
