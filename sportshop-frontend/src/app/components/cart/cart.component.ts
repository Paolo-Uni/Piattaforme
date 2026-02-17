import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
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
  indirizzoSpedizione: string = '';
  messaggio: string = '';
  errore: string = '';

  constructor(private cartService: CartService) { }

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cartItems = data;
        this.calcolaTotale();
      },
      error: (err) => console.error(err)
    });
  }

  incrementa(id: number): void {
    this.cartService.increaseQuantity(id).subscribe(() => this.loadCart());
  }

  decrementa(id: number): void {
    this.cartService.decreaseQuantity(id).subscribe(() => this.loadCart());
  }

  rimuovi(id: number): void {
    if(confirm('Sei sicuro di voler rimuovere questo prodotto dal carrello?')) {
      this.cartService.removeItem(id).subscribe(() => this.loadCart());
    }
  }

  svuota(): void {
    if(confirm('Sei sicuro di voler svuotare il carrello?')) {
      this.cartService.clearCart().subscribe(() => this.loadCart());
    }
  }

  ordina(): void {
    if (!this.indirizzoSpedizione || this.indirizzoSpedizione.trim() === '') {
      this.errore = 'Inserisci un indirizzo di spedizione valido.';
      return;
    }

    this.cartService.checkout(this.indirizzoSpedizione).subscribe({
      next: (res) => {
        this.messaggio = res.message || 'Ordine effettuato con successo!';
        this.errore = '';
        this.cartItems = []; // Svuota vista locale
        this.indirizzoSpedizione = '';
      },
      error: (err) => {
        this.errore = err.error?.message || 'Errore durante l\'ordine.';
        this.messaggio = '';
      }
    });
  }

  get totaleCarrello(): number {
    return this.cartItems.reduce((acc, item) => acc + (item.prezzoUnitario * item.quantita), 0);
  }

  // Metodo dummy per compatibilità se usato nel template precedente
  calcolaTotale() {}
}
