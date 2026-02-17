import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { FormsModule } from '@angular/forms';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {

  product: Product | null = null;
  quantity: number = 1;
  message: string = '';
  isError: boolean = false;
  isLoading: boolean = false; // Partiamo da false, attiviamo solo se abbiamo un ID
  isBrowser: boolean;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private oauthService: OAuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    // Iscrizione ai parametri URL
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');

      if (idParam && !isNaN(Number(idParam))) {
        this.loadProduct(Number(idParam));
      } else {
        this.isError = true;
        this.message = 'ID Prodotto non valido';
      }
    });
  }

  loadProduct(id: number): void {
    this.isLoading = true; // Inizio caricamento
    this.isError = false;

    this.productService.getProductById(id).subscribe({
      next: (data) => {
        this.product = data;
        this.isLoading = false; // FINE caricamento successo
      },
      error: (err) => {
        console.error('Errore dettaglio prodotto:', err);
        this.message = 'Prodotto non trovato o errore di connessione.';
        this.isError = true;
        this.isLoading = false; // FINE caricamento errore (FONDAMENTALE)
      }
    });
  }

  addToCart(): void {
    // Verifica browser-side per OAuth
    if (this.isBrowser) {
      if (!this.oauthService.hasValidAccessToken()) {
        this.oauthService.initCodeFlow();
        return;
      }
    }

    if (this.product) {
      this.cartService.addToCart(this.product.id, this.quantity).subscribe({
        next: () => {
          this.message = 'Prodotto aggiunto al carrello!';
          this.isError = false;
          setTimeout(() => this.message = '', 3000);
        },
        error: (err) => {
          this.message = err.error?.message || 'Errore durante l\'aggiunta.';
          this.isError = true;
        }
      });
    }
  }
}
