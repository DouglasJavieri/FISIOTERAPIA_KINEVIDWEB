/**
 * Modelo de respuesta de error del backend.
 * Espejo del ErrorResponse.java del backend.
 *
 * Se usa en http.util.ts → defaultHttpError() para normalizar
 * todos los errores HTTP en un formato consistente.
 */
export interface ErrorResponse {
  code: string;
  message: string;
  detail?: string;
  timestamp?: string;
  path: string;
  status: number;
}

