import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { Product } from '../../models/product.model';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.component.html'
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private keycloak: KeycloakService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productService.getProductById(Number(id)).subscribe(p => this.product = p);
    }
  }

  addToCart() {
    if (this.product) {
      this.cartService.addToCart(this.product.id, 1).subscribe({
        next: () => alert('Prodotto aggiunto!'),
        error: (err) => {
          if (err.status === 401) this.keycloak.login();
          else alert(err.error?.message);
        }
      });
    }
  }
}
