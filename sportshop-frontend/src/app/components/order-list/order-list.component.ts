import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe(data => this.orders = data);
  }

  cancel(id: number): void {
    if (confirm('Sei sicuro di voler annullare questo ordine?')) {
      this.orderService.cancelOrder(id, 'Annullato dall\'utente').subscribe(() => {
        this.ngOnInit(); // Ricarica la lista
      });
    }
  }
}
