import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = 'http://localhost:8082/ordini'; // Controlla la porta

  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/me`);
  }

  // Metodo per annullare l'ordine (se permesso dal backend)
  cancelOrder(id: number, motivo: string): Observable<void> {
    // Nota: Il backend potrebbe richiedere un body o un parametro.
    // Assumo una POST o PUT con il motivo. Adatta in base al tuo OrdineController.
    return this.http.post<void>(`${this.apiUrl}/${id}/annulla`, { motivo });
  }
}
