export interface Product {
  id: number;
  nome: string;
  descrizione: string;
  colore: string;
  taglia: string;
  stock: number;
  prezzo: number;
  categoria: string;
  marca: string;
}

// Interfaccia per mappare la risposta di Spring Data (Page<T>)
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  size: number;
  number: number;
  sort: any;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
