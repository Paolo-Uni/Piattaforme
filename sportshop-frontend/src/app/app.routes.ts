import { Routes } from '@angular/router';
import { ProductListComponent } from './components/product-list/product-list.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { CartComponent } from './components/cart/cart.component';
import { ProfileComponent } from './components/profile/profile.component';
import { OrderListComponent } from './components/order-list/order-list.component';

export const routes: Routes = [
  { path: '', redirectTo: 'prodotti', pathMatch: 'full' },
  { path: 'prodotti', component: ProductListComponent },
  { path: 'prodotti/:id', component: ProductDetailComponent },
  { path: 'carrello', component: CartComponent },
  { path: 'profilo', component: ProfileComponent },
  { path: 'ordini', component: OrderListComponent },
  { path: '**', redirectTo: 'prodotti' }
];
