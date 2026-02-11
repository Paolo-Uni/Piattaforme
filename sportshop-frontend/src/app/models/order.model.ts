export interface Order {
  idOrdine: number;
  data: string; // LocalDateTime arriva come stringa ISO
  stato: string;
  totaleOrdine: number;
  oggetti: OrderItem[];
}

export interface OrderItem {
  idOggetto: number;
  nome: string;
  taglia: string;
  colore: string;
  prezzo: number;
  quantita: number;
}
