export interface User {
  id?: number;
  nome: string;
  cognome: string;
  email: string;
  telefono?: string;
  indirizzo?: string; // Aggiunto per l'update profilo
}

export interface UserUpdateRequest {
  nome?: string;
  cognome?: string;
  telefono?: string;
  indirizzo?: string;
}
