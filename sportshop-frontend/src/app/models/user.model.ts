export interface User {
  id?: number;
  nome: string;
  cognome: string;
  email: string;
  telefono?: string; // Opzionale perché l'utente può rimuoverlo
  indirizzo?: string; // Opzionale
}

export interface UserUpdateRequest {
  nome?: string;
  cognome?: string;
  telefono?: string;
  indirizzo?: string;
}
