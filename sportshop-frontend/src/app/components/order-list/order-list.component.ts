import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

  orders: Order[] = [];
  messaggio: string = '';
  errore: string = '';

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getMyOrders().subscribe({
      next: (data) => this.orders = data,
      error: (err) => console.error(err)
    });
  }

  annullaOrdine(id: number): void {
    const motivo = prompt("Inserisci il motivo dell'annullamento:");
    if (motivo !== null) { // Se l'utente non preme Annulla nel prompt
      // Anche se motivo è stringa vuota, lo inviamo
      const motivoFinale = motivo.trim() === '' ? 'Nessun motivo specificato' : motivo;

      this.orderService.cancelOrder(id, motivoFinale).subscribe({
        next: (res) => {
          this.messaggio = res.message || 'Ordine annullato.';
          this.loadOrders();
        },
        error: (err) => {
          this.errore = err.error?.message || 'Impossibile annullare l\'ordine.';
        }
      });
    }
  }
}
