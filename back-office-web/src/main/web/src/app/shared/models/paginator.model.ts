/**
 * Modelo genérico de respuesta paginada del backend (Spring Data Page<T>).
 * El backend devuelve este formato en todos los endpoints paginados.
 *
 * Incluye los campos internos de Spring (pageable, sort, numberOfElements)
 * para compatibilidad total con la respuesta real de la API.
 */
export interface Paginator<T> {
  content: T[];
  pageable?: Pageable;
  last: boolean;
  totalPages: number;
  totalElements: number;
  sort?: PageSort;
  first: boolean;
  numberOfElements?: number;
  size: number;
  number: number;
  empty: boolean;
}

/** Información de paginación interna devuelta por Spring Data */
export interface Pageable {
  sort: PageSort;
  offset: number;
  pageSize: number;
  pageNumber: number;
  paged: boolean;
  unpaged: boolean;
}

/** Información de ordenamiento devuelta por Spring Data */
export interface PageSort {
  sorted: boolean;
  unsorted: boolean;
  empty: boolean;
}

/**
 * Crea un paginador vacío genérico para manejar respuestas nulas del backend
 * sin romper el flujo de la tabla.
 */
export const buildEmptyPaginator = <T>(number: number, size: number): Paginator<T> => ({
  content: [],
  empty: true,
  first: false,
  last: false,
  number,
  numberOfElements: 0,
  size,
  totalElements: 0,
  totalPages: 0,
});

