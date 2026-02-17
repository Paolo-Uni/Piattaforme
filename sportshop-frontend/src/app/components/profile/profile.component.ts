import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { FormsModule } from '@angular/forms';

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
  formData: any = {};
  messaggio = '';

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getMe().subscribe(u => {
      this.user = u;
      this.resetForm();
    });
  }

  resetForm(): void {
    if (this.user) {
      this.formData = { ...this.user };
    }
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
    if (!this.editMode) this.resetForm();
  }

  save(): void {
    this.userService.updateProfile(this.formData).subscribe(u => {
      this.user = u;
      this.editMode = false;
      this.messaggio = 'Profilo aggiornato!';
    });
  }
}
