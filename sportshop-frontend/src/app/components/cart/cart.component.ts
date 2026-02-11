import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { CartItem } from '../../models/cart.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  address = '';

  constructor(private cartService: CartService) {}

  ngOnInit() { this.load(); }

  load() {
    this.cartService.getCartItems().subscribe(items => this.cartItems = items);
  }

  updateQty(item: CartItem, delta: number) {
    const obs = delta > 0
      ? this.cartService.incrementQuantity(item.idProdotto)
      : this.cartService.decrementQuantity(item.idProdotto);

    obs.subscribe(() => this.load());
  }

  checkout() {
    if (!this.address) return;
    this.cartService.checkout(this.address).subscribe({
      next: () => {
        alert('Ordine confermato!');
        this.cartItems = [];
      },
      error: (err) => alert('Errore: ' + err.error?.message)
    });
  }
}
