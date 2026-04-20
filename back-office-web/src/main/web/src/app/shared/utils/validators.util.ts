import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Valida que el valor no sea únicamente espacios en blanco.
 * Retorna error { onlyWhitespace: true } si el campo contiene solo espacios.
 */
export function noOnlyWhitespaceValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string = control.value;
    if (!value) return null;
    return value.trim().length === 0
      ? { onlyWhitespace: true }
      : null;
  };
}

/**
 * Valida que el valor no empiece con espacios en blanco.
 * Retorna error { leadingWhitespace: true } si el campo empieza con espacio.
 */
export function noLeadingWhitespaceValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string = control.value;
    if (!value) return null;
    return value.startsWith(' ')
      ? { leadingWhitespace: true }
      : null;
  };
}

/**
 * Validador combinado: no solo espacios Y no espacios al inicio.
 * Úsalo cuando quieras ambas validaciones en un solo validator.
 * Prioridad: primero valida "solo espacios", luego "espacios al inicio".
 */
export function noWhitespaceValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: string = control.value;
    if (!value) return null;
    if (value.trim().length === 0) return { onlyWhitespace: true };
    if (value.startsWith(' '))     return { leadingWhitespace: true };
    return null;
  };
}
