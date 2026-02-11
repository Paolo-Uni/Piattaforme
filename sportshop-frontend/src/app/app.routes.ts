import { Routes } from '@angular/router';
import { ProductListComponent } from './components/product-list.component';
import { CartComponent } from './components/cart.component';
import { OrderListComponent } from './components/order-list/order-list.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AuthGuard } from './guards/auth.guard'; // Opzionale se usi KeycloakAuthGuard

export const routes: Routes = [
  { path: '', redirectTo: 'products', pathMatch: 'full' },
  { path: 'products', component: ProductListComponent },

  // Rotte protette (richiedono login)
  { path: 'cart', component: CartComponent, canActivate: [AuthGuard] },
  { path: 'orders', component: OrderListComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] }
];
