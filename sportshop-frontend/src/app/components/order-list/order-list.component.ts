import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

  orders: Order[] = [];
  errorMessage: string = '';
  infoMessage: string = '';

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => this.orders = data,
      error: (err) => this.errorMessage = 'Errore caricamento ordini.'
    });
  }

  cancelOrder(orderId: number): void {
    // Prompt semplice per chiedere il motivo
    const motivo = prompt("Inserisci il motivo dell'annullamento:");

    if (motivo !== null && motivo.trim() !== "") {
      this.orderService.cancelOrder(orderId, motivo).subscribe({
        next: (res) => {
          this.infoMessage = 'Ordine annullato con successo. Il rimborso è in elaborazione.';
          this.loadOrders(); // Ricarica la lista per vedere lo stato aggiornato
        },
        error: (err) => {
          this.errorMessage = err.error.message || 'Errore durante l\'annullamento.';
        }
      });
    } else if (motivo === "") {
      alert("Il motivo è obbligatorio per annullare l'ordine.");
    }
  }
}
