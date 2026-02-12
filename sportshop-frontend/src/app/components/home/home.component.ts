import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // <--- FONDAMENTALE

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule], // <--- SENZA QUESTO I LINK NON VANNO
  templateUrl: './home.component.html',
  // styleUrls: ...
})
export class HomeComponent {}
