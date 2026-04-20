package com.fisioterapiakinevid.kinevid.rest.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatUtil {
    public static final String MSG_TITLE_ERROR="ERROR";

    public static String defaultError(){
        return "Ocurrió un error inesperado";
    }

    public static String requerido(String campo){
        return " '"+campo+"'.";
    }

    public static String yaRegistrado(String objeto,String campo,String valor){
        return yaRegistrado(objeto,campo,valor,null);
    }

    public static String yaRegistrado(String objeto,String campo,String valor, Integer fila){
        return "Ya se encuentra registrado un '"+objeto+"' con el campo '"+campo+"'="+valor+(fila!=null?", para la fila: "+fila:"");
    }

    public static String coincidencia(String valor01,String valor02){
        return "El campo '"+valor01+"' no coincide con el campo '"+valor02+"'.";
    }

    public static String noRegistrado(String objeto,Long id){
        return "No se encontró registrado un '"+objeto+"' con el identificador'"+id+"'.";
    }
    public static String noRegistrado(String objeto,short id){
        return "No se encontró registrado un '"+objeto+"' con el identificador '"+id+"'.";
    }

    public static String noRegistrado(String objeto, String campo, String valor){
        return "No se encontró registrado un '"+objeto+"' con '" + campo + "': "+valor+".";
    }

    public static String noRegistrado(String objeto,Integer id){
        return "No se encontr\u00f3 registrado un '"+objeto+"' tipo '"+id+"'.";
    }

    public static String noHabilitado(String objeto,Long id){
        return "El '"+objeto+"' con el identificador'"+id+"' no se encuentra habilitado.";
    }

    public static String noHabilitado(String objeto, String accion) {
        return "El objeto '"+objeto+"' no se encuentra habilitado para" + accion;
    }

    public static String tipoIncorrecto(String campo,String tipo){
        return "El campo '"+campo+"' no es del tipo correcto '"+tipo+"'.";
    }

    public static String sinValor(String campo, String objeto, Long id) {
        return "El campo '" + campo + "' del objeto '" + objeto + "' con id " + id + " no tiene valor asignado";
    }

    public static String yaHabilitado(String objeto, Long id) {
        return "El '"+objeto+"' con el identificador'"+id+"' ya se encuentra habilitado.";
    }

    public static String yaInhabilitado(String objeto, Long id) {
        return "El '"+objeto+"' con el identificador'"+id+"' ya se encuentra inhabilitado.";
    }

    public static String noRegistrado(String objeto,String id){
        return "No se encontr\u00f3 registrado un '"+objeto+"' tipo '"+id+"'.";
    }

    public static String menorQue(String campo1,String campo2){
        return "El campo '"+campo1+"' debe ser menor al campo '"+campo2+"'.";
    }

    public static String toDateGreaterThanFrom(Date toDate, Date fromDate){
        return "El mes de " + getMonth(toDate)+ " es superior al de " +getMonth(fromDate)+ ", favor escoger rangos del mismo mes";
    }

    public static String noDataEncountered_Reports() {
        return "Reporte no generado, no existen datos relacionados";
    }


    private static String getMonth(Date date) {
        if (date == null) return null;

        String[] meses = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int monthNumber = calendar.get(Calendar.MONTH);

        String monthName = meses[monthNumber];
        return monthName;
    }

    public static String getYearMonthDayAndTime(Date date) {
        if (date == null) return null;

        SimpleDateFormat exitFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return exitFormat.format(date);
    }
    public static String passwordResetFailed(String keycloakId){
        return "Fallo al restablecer la contraseña del usuario: " + keycloakId;
    }

}
