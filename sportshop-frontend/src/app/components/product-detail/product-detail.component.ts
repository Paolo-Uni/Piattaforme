import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { OAuthService } from 'angular-oauth2-oidc';
import { Product } from '../../models/product.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  quantita = 1;
  messaggio = '';
  errore = '';

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private oauthService: OAuthService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProductById(id).subscribe({
      next: (p) => this.product = p,
      error: () => this.errore = 'Prodotto non trovato'
    });
  }

  addToCart(): void {
    if (!this.oauthService.hasValidAccessToken()) {
      this.oauthService.initCodeFlow();
      return;
    }
    if (this.product) {
      this.cartService.addToCart(this.product.id, this.quantita).subscribe({
        next: () => this.messaggio = 'Prodotto aggiunto al carrello!',
        error: (err) => this.errore = err.error?.message || 'Errore'
      });
    }
  }
}
