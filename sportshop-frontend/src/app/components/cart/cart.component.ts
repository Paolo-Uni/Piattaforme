import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { CartItem } from '../../models/cart.model';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  cartItems: CartItem[] = [];
  indirizzoSpedizione: string = '';
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private cartService: CartService, private router: Router) { }

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (data) => {
        this.cartItems = data;
        this.calculateTotal();
      },
      error: (err) => console.error('Errore caricamento carrello', err)
    });
  }

  calculateTotal(): number {
    return this.cartItems.reduce((acc, item) => acc + (item.prezzoUnitario * item.quantita), 0);
  }

  increment(id: number): void {
    this.cartService.increaseQuantity(id).subscribe({
      next: () => this.loadCart(),
      error: (err) => this.errorMessage = err.error.message || 'Errore durante l\'aggiornamento'
    });
  }

  decrement(id: number): void {
    // Trova l'item per controllare la quantità locale prima di chiamare il backend
    const item = this.cartItems.find(i => i.idProdotto === id);
    if (item && item.quantita > 1) {
      this.cartService.decreaseQuantity(id).subscribe({
        next: () => this.loadCart(),
        error: (err) => this.errorMessage = err.error.message
      });
    } else {
      // Se è 1, chiediamo conferma prima di rimuovere (o chiamiamo direttamente remove)
      this.remove(id);
    }
  }

  // NUOVO METODO: Rimuove tutto il prodotto
  remove(id: number): void {
    if(confirm('Sei sicuro di voler rimuovere questo prodotto dal carrello?')) {
      this.cartService.removeItem(id).subscribe({
        next: () => {
          this.loadCart();
          this.successMessage = 'Prodotto rimosso.';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err) => this.errorMessage = 'Impossibile rimuovere il prodotto.'
      });
    }
  }

  checkout(): void {
    if (!this.indirizzoSpedizione || this.indirizzoSpedizione.trim() === '') {
      this.errorMessage = 'Inserisci un indirizzo di spedizione valido.';
      return;
    }

    this.cartService.checkout(this.indirizzoSpedizione).subscribe({
      next: (res) => {
        alert('Ordine effettuato con successo!');
        this.cartItems = [];
        this.router.navigate(['/ordini']); // Reindirizza agli ordini
      },
      error: (err) => {
        this.errorMessage = err.error.message || 'Errore durante il checkout.';
      }
    });
  }
}
