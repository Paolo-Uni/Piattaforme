import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../services/cart.service';
import { CartItem } from '../models/cart.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <h2>Il tuo Carrello</h2>
      @if (cartItems.length === 0) {
        <p>Il carrello è vuoto.</p>
      } @else {
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Prodotto</th>
              <th>Prezzo</th>
              <th>Quantità</th>
              <th>Totale</th>
              <th>Azioni</th>
            </tr>
          </thead>
          <tbody>
            @for (item of cartItems; track item.idProdotto) {
              <tr>
                <td>{{ item.nomeProdotto }} ({{ item.taglia }}, {{ item.colore }})</td>
                <td>{{ item.prezzoUnitario | currency:'EUR' }}</td>
                <td>
                   <button class="btn btn-sm btn-secondary mx-1" (click)="decrease(item)">-</button>
                   {{ item.quantita }}
                   <button class="btn btn-sm btn-secondary mx-1" (click)="increase(item)">+</button>
                </td>
                <td>{{ item.prezzoUnitario * item.quantita | currency:'EUR' }}</td>
                <td></td>
              </tr>
            }
          </tbody>
        </table>

        <div class="card p-3 mt-3">
            <h4>Checkout</h4>
            <div class="mb-3">
                <label for="address" class="form-label">Indirizzo di Spedizione</label>
                <input type="text" class="form-control" id="address" [(ngModel)]="shippingAddress" placeholder="Via Roma 1, Milano">
            </div>
            <button class="btn btn-success" [disabled]="!shippingAddress" (click)="checkout()">Ordina e Paga</button>
        </div>
      }
    </div>
  `
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  shippingAddress = '';

  constructor(private cartService: CartService) {}

  ngOnInit() { this.loadCart(); }

  loadCart() {
    this.cartService.getCartItems().subscribe(items => this.cartItems = items);
  }

  increase(item: CartItem) {
    this.cartService.incrementQuantity(item.idProdotto).subscribe(() => this.loadCart());
  }

  decrease(item: CartItem) {
    this.cartService.decrementQuantity(item.idProdotto).subscribe(() => this.loadCart());
  }

  checkout() {
    if (!this.shippingAddress) return;
    this.cartService.checkout(this.shippingAddress).subscribe({
      next: () => {
        alert('Ordine effettuato con successo!');
        this.cartItems = [];
        this.shippingAddress = '';
      },
      error: (err) => alert('Errore checkout: ' + err.error?.message)
    });
  }
}
