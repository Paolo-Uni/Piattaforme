export interface Product {
  id: number;
  nome: string;
  descrizione: string;
  prezzo: number;
  stock: number;
  categoria: string;
  marca: string;
  colore: string;
  taglia: string;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
