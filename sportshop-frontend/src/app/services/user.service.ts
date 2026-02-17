import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserUpdateRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8082/cliente';

  constructor(private http: HttpClient) { }

  // Registrazione
  register(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/registra`, user);
  }

  // Ottieni profilo loggato (ritorna ClienteDTO)
  getMe(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  // Aggiorna profilo
  updateProfile(data: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me/update`, data);
  }

  // Admin: Ottieni tutti
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
