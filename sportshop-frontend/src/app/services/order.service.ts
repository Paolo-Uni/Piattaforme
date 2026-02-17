import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private apiUrl = 'http://localhost:8082/ordini';

  constructor(private http: HttpClient) { }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/miei-ordini`);
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  // Modificato: ora accetta solo ID e stringa motivo
  cancelOrder(orderId: number, motivo: string): Observable<any> {
    const body = { motivo: motivo };
    return this.http.post(`${this.apiUrl}/annulla/${orderId}`, body);
  }
}
