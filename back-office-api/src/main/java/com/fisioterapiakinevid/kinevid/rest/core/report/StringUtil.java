package com.fisioterapiakinevid.kinevid.rest.core.report;

import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

/*
 *----------------------------------------
 *   Código de Aplicación:
 *   Código de Objeto:
 *   Descripción:
 *   Author Prog: Jorge Luis Choque Callizaya
 *----------------------------------------
 *   Fecha | Autor | Comentario
 *   12.08.2026 | Jorge Luis Choque Callizaya | Creación Inicial
 *----------------------------------------
 */
public class StringUtil {

    public static boolean isEmptyOrNull(String value) {
        if (value == null) {
            return true;
        }
        if (value.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static String showEmptyIfNull(Object value) {
        if (value == null) {
            return "";
        }else{
            return value.toString();
        }
    }

    public static boolean isBlank(String value) {
        return isEmptyOrNull(value);
    }

    public static String trimUpperCase(String valor) {
        if (valor == null) {
            return null;
        }
        valor = valor.trim();
        valor = valor.toUpperCase();
        return valor;
    }

    public String encode(String txt) {
        String base64 = byteToBase64(txt.getBytes());
        System.out.println(base64);
        System.out.println(base64.substring(0, base64.length() - 1));
        return base64.substring(0, base64.length() - 1);
    }

    public static String[] decode(String base64) {
        base64 += "=";
        System.out.println(base64);
        String txt = new String(base64ToByte(base64));
        return txt.split("\\|");
    }

    public static String byteToBase64(byte[] byteArray) {
        byte[] encoded = Base64.getEncoder().encode(byteArray);
        return new String(encoded);
    }

    public static byte[] base64ToByte(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        return decoded;
    }

    public static String getFirst(String path, String separator) {
        return Arrays.stream(path.split(Pattern.quote(separator)))
                .findFirst().orElse("");
    }

    public static String removeFirst(String path, String separator) {
        int index = path.indexOf(separator);
        return index > -1 ? path.substring(index + 1) : "";
    }
}

