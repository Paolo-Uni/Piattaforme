import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      <h2>I Miei Ordini</h2>
      @for (order of orders; track order.idOrdine) {
        <div class="card mb-3">
          <div class="card-header d-flex justify-content-between">
            <span>Ordine #{{ order.idOrdine }} - {{ order.data | date:'short' }}</span>
            <span class="badge bg-info text-dark">{{ order.stato }}</span>
          </div>
          <div class="card-body">
            <ul>
              @for (item of order.oggetti; track item.idOggetto) {
                <li>{{ item.nome }} - {{ item.quantita }}x {{ item.prezzo | currency:'EUR' }}</li>
              }
            </ul>
            <p class="fw-bold">Totale: {{ order.totaleOrdine | currency:'EUR' }}</p>

            @if (order.stato !== 'ANNULLATO' && order.stato !== 'SPEDITO') {
              <button class="btn btn-danger btn-sm" (click)="cancel(order.idOrdine)">Annulla Ordine</button>
            }
          </div>
        </div>
      }
      @if (orders.length === 0) { <p>Nessun ordine effettuato.</p> }
    </div>
  `
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.orderService.getMyOrders().subscribe(data => this.orders = data);
  }

  cancel(id: number) {
    const motivo = prompt("Inserisci motivo annullamento:");
    if (motivo) {
      this.orderService.cancelOrder(id, motivo).subscribe({
        next: () => {
          alert('Ordine annullato');
          this.ngOnInit(); // Ricarica lista
        },
        error: (err) => alert('Errore: ' + err.error?.message)
      });
    }
  }
}
