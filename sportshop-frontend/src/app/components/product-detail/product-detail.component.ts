import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { switchMap } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule], // Importante per ngIf e ngModel
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {

  product: Product | null = null;
  isLoading = true;
  errorMessage = '';
  quantitaSelezionata = 1;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    // switchMap annulla la chiamata precedente se l'utente cambia id velocemente
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('id'));
        this.isLoading = true;
        this.errorMessage = '';
        this.product = null; // Resetta il prodotto mentre carica il nuovo
        return this.productService.getProductById(id);
      })
    ).subscribe({
      next: (data) => {
        this.product = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Errore caricamento dettaglio', err);
        this.isLoading = false;
        if (err.status === 404) {
          this.errorMessage = 'Prodotto non trovato.';
        } else {
          this.errorMessage = 'Impossibile caricare il prodotto. Riprova più tardi.';
        }
      }
    });
  }

  aggiungiAlCarrello(): void {
    if (this.product) {
      this.cartService.addToCart(this.product.id, this.quantitaSelezionata).subscribe({
        next: () => {
          alert('Prodotto aggiunto al carrello!');
        },
        error: (err) => {
          console.error(err);
          alert('Errore durante l\'aggiunta al carrello: ' + (err.error?.message || 'Errore sconosciuto'));
        }
      });
    }
  }
}
