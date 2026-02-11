import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html'
})
export class AdminDashboardComponent {
  newProd: any = { nome: '', marca: '', categoria: '', prezzo: 0, stock: 0, taglia: 'U', colore: 'Nero', descrizione: '' };
  stockId = 0;
  stockQty = 0;

  constructor(private prodService: ProductService) {}

  create() {
    this.prodService.createProduct(this.newProd).subscribe({
      next: () => { alert('Prodotto Creato!'); this.newProd.nome = ''; }, // Reset semplice
      error: (e) => alert('Errore: ' + e.error?.message)
    });
  }

  addStock() {
    this.prodService.addStock(this.stockId, this.stockQty).subscribe({
      next: () => alert('Stock aggiornato!'),
      error: (e) => alert('Errore: ' + e.error?.message)
    });
  }
}
