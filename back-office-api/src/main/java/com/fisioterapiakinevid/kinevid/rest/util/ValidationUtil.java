package com.fisioterapiakinevid.kinevid.rest.util;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.exception.ValidationFieldException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ValidationUtil {
    public static final String DECIMAL_FORMAT = "####################.#####";


    public static boolean isNullOrZero(Long value) {
        return value == null || value.equals(0L);
    }

    public static void throwExceptionRequiredIfNull(String name, Object value) throws OperationException {
        if (value == null) {
            throw new OperationException(FormatUtil.requerido(name));
        }
    }

    public static void throwExceptionRequiredIfBlank(String name, String value) throws OperationException {
        if (value == null || value.trim().isEmpty()) {
            throw new OperationException(FormatUtil.requerido(name));
        }
    }

    public static void throwExceptionNotFoundIfNull(String name, Object value, String codigo) throws OperationException {
        if (value == null) {
            throw new OperationException("El campo '" + name + "' " + codigo + " no se encontrÃ³ registrado");
        }
    }

    public static void throwExceptionIfListNotUniqueResult(String name, List values, String codigo) throws OperationException {
        if (values == null || values.isEmpty()) {
            throw new OperationException("El campo '" + name + "' " + codigo + " no se encontrÃ³ registrado");
        }
        if (values.size() > 1) {
            throw new OperationException("El campo '" + name + "' " + codigo + " se encuentra registrado mÃ¡s de una vez");
        }
    }

    public static void throwExceptionIfListBlank(String name, List values, String codigo) throws OperationException {
        if (values == null || values.isEmpty()) {
            throw new OperationException("No existen '" + name + "' que coincidan con la bÃºsqueda de " + codigo);
        }
    }

    public static void throwExceptionIfInvalidNumber(String name, Long value, boolean required, Long greaterThan) throws OperationException {
        throwExceptionIfInvalidNumber(name, value, required, greaterThan, null);
    }

    public static void throwExceptionIfInvalidNumber(String name, Long value, boolean required, Long greaterThan, Long lessThan) throws OperationException {
        if (!required && value == null)
            return;

        if (required && value == null) {
            log.info("El campo '" + name + "' es requerido");
            throw new OperationException("El campo '" + name + "' es requerido");
        }
        if (value.compareTo(greaterThan) <= 0) { // value es menor o igual
            log.info("El campo '" + name + "' debe ser mayor que " + greaterThan);
            throw new OperationException("El campo '" + name + "' debe ser mayor que " + greaterThan);
        }

        if (lessThan != null && value.compareTo(lessThan) >= 0) { // value es mayor o igual
            log.info("El campo '" + name + "' debe ser menor que " + lessThan);
            throw new OperationException("El campo '" + name + "' debe ser menor que " + lessThan);
        }
    }

    public static void throwExceptionIfInvalidNumber(String name, Integer value, boolean required, Integer greaterThan) throws OperationException {
        throwExceptionIfInvalidNumber(name, value, required, greaterThan, null);
    }

    public static void throwExceptionIfInvalidNumber(String name, Short value, boolean required, Integer greaterThan) throws OperationException {
        throwExceptionIfInvalidNumber(name, (int) value, required, greaterThan, null);
    }

    private static void throwExceptionIfInvalidNumber(String name, Integer value, boolean required, Integer greaterThan, Integer lessThan) throws OperationException {
        if (!required && value == null)
            return;

        if (required && value == null){

            throw new OperationException("El campo '" + name + "' es requerido");
        }
        if (value.compareTo(greaterThan) < 0) { // value es menor o igual
            throw new OperationException("El campo '" + name + "' debe ser mayor que " + greaterThan);
        }

        if (lessThan != null && value.compareTo(lessThan) <= 0) { // value es mayor o igual
            throw new OperationException("El campo '" + name + "' debe ser menor que " + lessThan);
        }
    }

    public static void ExceptionIfInvalidBigDecimal(String name, BigDecimal value, boolean required, BigDecimal greaterThan, int fractionDigits, int totalDigits) throws OperationException {
        if (!required && value == null)
            return;

        if (required && value == null)
            throw new OperationException("El campo '" + name + "' es requerido");

        if (value.compareTo(greaterThan) > 0) { // value es mayor a greaterThan
            if (BigDecimalUtil.getQuantityDecimals(value) > fractionDigits) {
                throw new OperationException("Para el campo '" + name + "' " + value + " la cantidad de decimales debe ser menor a " + (fractionDigits + 1));
            }
            if (BigDecimalUtil.getQuantityDigits(value) > totalDigits) {
                throw new OperationException("Para el campo '" + name + "' " + value + " la cantidad de dÃ­gitos debe ser menor a " + (totalDigits + 1));
            }
        } else if (value.compareTo(greaterThan) == 0) {
            throw new OperationException("El campo '" + name + "' debe ser mayor a " + greaterThan);
        } else {
            throw new OperationException("El campo '" + name + "' " + value + " debe ser mayor a " + greaterThan);
        }
    }

    public static void throwExceptionIfInvalidBigDecimal(String name, BigDecimal value, boolean required, BigDecimal greaterThan, int fractionDigits) throws OperationException {
        throwExceptionIfInvalidBigDecimal(name, value, required, greaterThan, false, fractionDigits);
    }
    public static void throwExceptionIfInvalidBigDecimal(String name, BigDecimal value, boolean required, BigDecimal greaterThan, int fractionDigits, int totalDigits) throws OperationException {
        throwExceptionIfInvalidBigDecimal(name, value, required, greaterThan, false, fractionDigits, totalDigits);
    }
    public static void throwExceptionIfInvalidBigDecimal(String name, BigDecimal value, boolean required, BigDecimal greaterThan, boolean permitEqualGreater, Integer fractionDigits, Integer totalDigits) throws OperationException {
        if (!required && value == null)
            return;

        if (required && value == null)
            throw new OperationException("El campo '" + name + "' es requerido");

        if (greaterThan != null) {
            if (value.compareTo(greaterThan) > 0) { // value es mayor a greaterThan
                if (fractionDigits != null && BigDecimalUtil.getQuantityDecimals(value) > fractionDigits) {
                    throw new OperationException("Para el campo '" + name + "' " + value + " la cantidad de decimales debe ser menor a " + (fractionDigits + 1));
                }
                if (totalDigits != null && BigDecimalUtil.getQuantityDigits(value) > totalDigits) {
                    throw new OperationException("Para el campo '" + name + "' " + value + " la cantidad de dÃ­gitos debe ser menor a " + (totalDigits + 1));
                }
            } else if (value.compareTo(greaterThan) == 0) {
                if (!permitEqualGreater) {
                    throw new OperationException("El campo '" + name + "' debe ser mayor a " + greaterThan);
                }
            } else {
                throw new OperationException("El campo '" + name + "' " + value + " debe ser mayor a " + greaterThan);
            }
        }
    }

    public static void throwExceptionIfInvalidBigDecimal(String name, BigDecimal value, boolean required, BigDecimal greaterThan, boolean equalToGreaterThan, int fractionDigits) throws OperationException {
        if (!required && value == null)
            return;

        if (required && value == null)
            throw new OperationException("El campo '" + name + "' es requerido");

        if (value.compareTo(greaterThan) > 0) { // value es mayor a greaterThan
            if (BigDecimalUtil.getQuantityDecimals(value) > fractionDigits) {
                throw new OperationException("Para el campo '" + name + "' la cantidad de decimales debe ser menor a " + (fractionDigits + 1));
            }

        } else if (value.compareTo(greaterThan) == 0) {
            if (!equalToGreaterThan)
                throw new OperationException("El campo '" + name + "' debe ser mayor a " + greaterThan);
        } else {
            throw new OperationException("El campo '" + name + "' debe ser mayor a " + greaterThan);
        }
    }


    public static BigDecimal throwExceptionIfInvalidBigdecimal(String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new OperationException(FormatUtil.requerido(name));
        }

        try {
            NumberFormat numberFormat = new DecimalFormat(DECIMAL_FORMAT);
            numberFormat.parse(value.trim());
            return new BigDecimal(value.trim());
        } catch (ParseException e) {
            throw new OperationException("No logrÃ³ analizar gramaticalemente a BigDecimal el campo '" + name + "'");
        }
    }

    public static void throwExceptionIfInvalidTextRegex(String name, String value, String regex) throws OperationException {
        if (!value.matches(regex)) {
            throw new ValidationFieldException("El campo '" + name + "' no tiene el formato adecuado");
        }
    }


    public static void throwExceptionIfInvalidText(String name, String value, boolean required, int maxLength) throws OperationException {
        throwExceptionIfInvalidText(name, value, required, 0, maxLength);
    }

    public static void throwExceptionIfInvalidText(String name, String value, boolean required, int minLength, int maxLength) throws OperationException {
        if (!required && (value == null || value.trim().isEmpty())) {
            return;
        }
        if (required && (value == null || value.trim().isEmpty())) {
            throw new OperationException(FormatUtil.requerido(name));
        }

        if (minLength > 0 && value.trim().length() < minLength) {
            throw new OperationException("Para el campo '" + name + "' la longitud mÃ­nima debe ser " + minLength);
        }
        if (value.trim().length() > maxLength) {
            throw new OperationException("Para el campo '" + name + "' la longitud mÃ¡xima debe ser " + maxLength);
        }
    }

    public static void throwExceptionIfInvalidEmail(String name, String value, boolean required) throws OperationException {
        if (!required && (value == null || value.trim().isEmpty())) {
            return;
        }
        if (required && (value == null || value.trim().isEmpty())) {
            throw new OperationException(FormatUtil.requerido(name));
        }

        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        String email = value.trim();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            if (name == null || name.trim().isEmpty())
                throw new OperationException("Formato de correo electrÃ³nico no vÃ¡lido");
            else
                throw new OperationException("El campo '" + name + "' tiene un formato no vÃ¡lido");
        }
    }

    public static void throwExceptionIfInvalidString(String name, String value, String pattern) throws OperationException {
        if (!value.matches(pattern)) {
            throw new OperationException("El campo '" + name + "' no coincide con el formato permitido, verifique que el valor no contenga acentos ni otros caracteres especiales.");
        }
    }

    public static void throwExceptionIfInvalidDate(String name, String value, boolean required, String format) throws OperationException {
        if (required && value == null) {
            throw new OperationException(FormatUtil.requerido(name));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try{
            dateFormat.parse(value);
        }catch (ParseException e){
            throw new OperationException("Para el campo '" + name + "' " + value + " no tiene el formato " + format);
        }
    }

    public static void throwExceptionIfRequiredLong(String name, Long value, boolean required) throws OperationException {
        if (required && value == null) {
            throw new OperationException(FormatUtil.requerido(name));
        }
    }

    public static void throwExceptionIfInvalidDate(String name, Date value, boolean required) throws OperationException {
        if (required && value == null) {
            throw new OperationException(FormatUtil.requerido(name));
        }
    }

    public static void throwExceptionIfInvalidDate(String inicioDeCirugÃ­a, java.sql.Date dateTimeSurgeryStart) {
    }
}

