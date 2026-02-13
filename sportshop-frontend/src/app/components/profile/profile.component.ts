import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User, UserUpdateRequest } from '../../models/user.model';
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
  message: string = '';
  isEditing: boolean = false;

  // Dati per il form di modifica
  editData: UserUpdateRequest = {};

  constructor(private userService: UserService, private oauthService: OAuthService) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.userService.getMe().subscribe({
      next: (data) => {
        this.user = data;
        // Inizializza i dati del form
        this.editData = {
          nome: data.nome,
          cognome: data.cognome,
          telefono: data.telefono,
          indirizzo: data.indirizzo
        };
      },
      error: (err) => {
        console.error(err);
        this.message = 'Impossibile caricare il profilo.';
      }
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    this.message = '';
  }

  saveProfile(): void {
    this.userService.updateProfile(this.editData).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.message = 'Profilo aggiornato con successo!';
        this.isEditing = false;
      },
      error: (err) => {
        this.message = err.error.message || 'Errore durante l\'aggiornamento.';
      }
    });
  }

  logout(): void {
    this.oauthService.logOut();
  }
}
