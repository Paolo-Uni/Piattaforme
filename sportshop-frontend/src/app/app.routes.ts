import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { CartComponent } from './components/cart/cart.component';
import { ProfileComponent } from './components/profile/profile.component';
import { OrderListComponent } from './components/order-list/order-list.component';
import { authGuard } from './guards/auth.guard'; // Se hai una guard

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'prodotti', component: ProductListComponent },
  { path: 'prodotti/:id', component: ProductDetailComponent },
  { path: 'carrello', component: CartComponent, canActivate: [authGuard] }, // Protetto
  { path: 'profilo', component: ProfileComponent, canActivate: [authGuard] }, // Protetto
  { path: 'ordini', component: OrderListComponent, canActivate: [authGuard] }, // Protetto
  { path: '**', redirectTo: '' }
];
