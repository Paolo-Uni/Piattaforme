import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserUpdateRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8082/clienti';

  constructor(private http: HttpClient) { }

  // Registrazione (pubblica, non richiede token)
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/registra`, userData);
  }

  // Ottieni il profilo dell'utente loggato
  getMe(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  // Aggiorna i dati del profilo
  updateProfile(data: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me`, data);
  }
}
