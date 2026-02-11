import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-list.component.html'
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  loading = true;

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  cancel(id: number) {
    const motivo = prompt("Inserisci il motivo dell'annullamento:");
    if (motivo) {
      this.orderService.cancelOrder(id, motivo).subscribe({
        next: () => {
          alert('Ordine annullato con successo.');
          this.loadOrders(); // Ricarica la lista per vedere lo stato aggiornato
        },
        error: (err) => alert('Errore: ' + (err.error?.message || err.message))
      });
    }
  }
}
