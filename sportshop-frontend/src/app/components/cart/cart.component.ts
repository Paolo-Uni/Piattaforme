import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { UserService } from '../../services/user.service';
import { CartItem } from '../../models/cart.model';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  indirizzoSpedizione = '';
  messaggio = '';
  errore = '';

  constructor(private cartService: CartService, private userService: UserService) { }

  ngOnInit(): void {
    this.loadCart();
    this.userService.getMe().subscribe(u => {
      if (u.indirizzo) this.indirizzoSpedizione = u.indirizzo;
    });
  }

  loadCart(): void {
    this.cartService.getCart().subscribe(items => this.cartItems = items);
  }

  incrementa(id: number): void { this.cartService.increaseQuantity(id).subscribe(() => this.loadCart()); }
  decrementa(id: number): void { this.cartService.decreaseQuantity(id).subscribe(() => this.loadCart()); }
  rimuovi(id: number): void { this.cartService.removeItem(id).subscribe(() => this.loadCart()); }

  checkout(): void {
    if (!this.indirizzoSpedizione) {
      this.errore = 'Inserisci un indirizzo di spedizione.';
      return;
    }
    this.cartService.checkout(this.indirizzoSpedizione).subscribe({
      next: () => {
        this.messaggio = 'Ordine effettuato con successo!';
        this.cartItems = [];
      },
      error: (err) => this.errore = err.error?.message || 'Errore ordine'
    });
  }

  get totale(): number {
    return this.cartItems.reduce((acc, item) => acc + (item.prezzoUnitario * item.quantita), 0);
  }
}
