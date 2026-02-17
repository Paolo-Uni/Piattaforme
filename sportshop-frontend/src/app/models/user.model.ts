export interface User {
  id?: number;
  nome: string;
  cognome: string;
  email: string;
  telefono?: string;
  // Nota: ClienteDTO dal backend attualmente non restituisce l'indirizzo nel GET /me
  indirizzo?: string;
}

export interface UserUpdateRequest {
  nome?: string;
  cognome?: string;
  telefono?: string;
  // Questo campo viene inviato se il form lo prevede, ma il backend deve essere predisposto nel DTO di update
  indirizzo?: string;
}
