import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4" *ngIf="user">
      <h2>Il mio Profilo</h2>
      <div class="mb-3">
        <label>Email</label>
        <input type="text" class="form-control" [value]="user.email" disabled>
      </div>
      <div class="mb-3">
        <label>Nome</label>
        <input type="text" class="form-control" [(ngModel)]="user.nome">
      </div>
      <div class="mb-3">
        <label>Cognome</label>
        <input type="text" class="form-control" [(ngModel)]="user.cognome">
      </div>
      <button class="btn btn-primary" (click)="save()">Salva Modifiche</button>
    </div>
  `
})
export class ProfileComponent implements OnInit {
  user: User | null = null;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.getProfile().subscribe(u => this.user = u);
  }

  save() {
    if (this.user) {
      this.userService.updateProfile({ nome: this.user.nome, cognome: this.user.cognome })
        .subscribe(() => alert('Profilo aggiornato!'));
    }
  }
}
