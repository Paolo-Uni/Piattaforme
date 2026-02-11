import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  isLoading = true;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: (u) => { this.user = u; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  save() {
    if (this.user) {
      this.userService.updateProfile({ nome: this.user.nome, cognome: this.user.cognome })
        .subscribe(() => alert('Profilo aggiornato con successo!'));
    }
  }
}
