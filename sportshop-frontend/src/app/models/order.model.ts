export interface OrderItem {
  idOggetto: number;
  nome: string; // Corrisponde a nomeProdotto nel backend
  taglia: string;
  colore: string;
  prezzo: number;
  quantita: number;
}

export interface Order {
  idOrdine: number;
  data: string; // Le date JSON arrivano come stringhe ISO
  stato: string;
  totaleOrdine: number;
  indirizzoSpedizione: string; // Campo essenziale aggiunto
  oggetti: OrderItem[];
}
