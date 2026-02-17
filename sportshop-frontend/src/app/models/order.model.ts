export interface OrderItem {
  idOggetto: number;
  nome: string;
  taglia: string;
  colore: string;
  prezzo: number;
  quantita: number;
}

export interface Order {
  idOrdine: number;
  data: string; // Date arriva come stringa ISO dal backend
  stato: string;
  totaleOrdine: number;
  oggetti: OrderItem[];
}
