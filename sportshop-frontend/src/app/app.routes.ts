import { Routes } from '@angular/router';
import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { CartComponent } from './components/cart/cart.component';
import { OrderListComponent } from './components/order-list/order-list.component';
import { ProfileComponent } from './components/profile/profile.component';
import { HomeComponent } from './components/home/home.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'prodotti', component: ProductListComponent },
  // Questa è la rotta fondamentale per i dettagli:
  { path: 'prodotti/:id', component: ProductDetailComponent },

  // Rotte protette
  { path: 'carrello', component: CartComponent, canActivate: [authGuard] },
  { path: 'ordini', component: OrderListComponent, canActivate: [authGuard] },
  { path: 'profilo', component: ProfileComponent, canActivate: [authGuard] },

  { path: '**', redirectTo: '' }
];
