import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User, UserUpdateRequest } from '../../models/user.model';
import { FormsModule } from '@angular/forms';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User | null = null;
  editMode = false;
  // DTO per l'aggiornamento
  updateData: UserUpdateRequest = {};

  messaggio = '';
  errore = '';

  constructor(private userService: UserService, private oauthService: OAuthService) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.userService.getMe().subscribe({
      next: (data) => {
        this.user = data;
        // Inizializza i dati del form
        this.updateData = {
          nome: data.nome,
          cognome: data.cognome,
          telefono: data.telefono,
          indirizzo: data.indirizzo
        };
      },
      error: (err) => console.error(err)
    });
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
    this.messaggio = '';
    this.errore = '';
  }

  salvaModifiche(): void {
    this.userService.updateProfile(this.updateData).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.messaggio = 'Profilo aggiornato con successo!';
        this.editMode = false;
      },
      error: (err) => {
        this.errore = err.error?.message || 'Errore durante l\'aggiornamento.';
      }
    });
  }

  logout(): void {
    this.oauthService.logOut();
  }
}
