package com.fisioterapiakinevid.kinevid.rest.core.report;

import lombok.extern.slf4j.Slf4j;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
@Slf4j
public class DateUtil {

    public static final String FORMAT_FECHA_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS"; // Definido por el SIN
    public static final String FORMAT_FACTURA = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String FORMAT_FACTURA_CUF = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_FACTURA_CUF_CONTINGENCIA = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String FORMAT_FILE = "yyyy-MM-dd HHmmss";
    public static final String FORMAT_FECHA_ESTADISTICA = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_TIME = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_DATE_PARAM_URL = "dd-MM-yyyy HH:mm:ss";
    public static final String FORMAT_DATE_BARRA = "dd/MM/yyyy";

    public static final String FORMAT_YYYYMMDDHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDDHHmmssSSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_DATE_DD_MM_YYYY = "dd-MM-yyyy";
    public static final String FORMAT_DATE_QR_DOM = "yyyy-MM-dd";
    public static final String FORMAT_YYMMDD = "yyMMdd";

    public static final String FORMAT_NAME_REPORT = "yyyyMMddHHmmssSSS";

    public static final String FORMAT_NAME_REPORT_RRHH = "ddMMyyyyHHmm";

    public static DateFromToRange validateAndParseDates(String fromDateStr, String toDateStr) {
        Date fromDate = null;
        Date toDate = null;

        if (fromDateStr == null || toDateStr == null || fromDateStr.isEmpty() || toDateStr.isEmpty()) {
            return null; // Fechas vacías o nulas
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            // Analizar y convertir la fecha de inicio
            if (!fromDateStr.isEmpty()) {
                fromDate = formatter.parse(fromDateStr);
                fromDate = setStartOfDay(fromDate); // Ajustar al inicio del día
            }

            // Analizar y convertir la fecha de fin
            if (!toDateStr.isEmpty()) {
                toDate = formatter.parse(toDateStr);
                toDate = setEndOfDay(toDate); // Ajustar al final del día
            }

            // Intercambiar si las fechas están invertidas
            if (fromDate != null && toDate != null && fromDate.after(toDate)) {
                Date temp = fromDate;
                fromDate = toDate;
                toDate = temp;

                fromDate = setStartOfDay(fromDate); // Ajustar al inicio del día
                toDate = setEndOfDay(toDate); // Ajustar al final del día
            }

            return new DateFromToRange(fromDate, toDate); // Retornar el rango de fechas
        } catch (Exception e) {
            System.err.println("Error al analizar la fecha: " + e.getMessage());
            return null; // Ocurrió un error
        }
    }

    private static Timestamp setStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Timestamp(cal.getTimeInMillis());
    }

    private static Timestamp setEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return new Timestamp(cal.getTimeInMillis());
    }

    public static Date getLastDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date getFirstDateOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date getFirstDateOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date formatToStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date formatToEnd(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date toDate(String format, String valor) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date valorDate = sdf.parse(valor);
            return valorDate;
        } catch (Exception ex) {
            log.error("Error ", ex);
            return null;
        }
    }

    public static String toString(String format, Date valor) {
        if (valor == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String valorString = sdf.format(valor);
            return valorString;
        } catch (Exception ex) {
            log.error("Error ", ex);
            return null;
        }
    }

    public static String toStringDate(Date valor) {
        return toString(FORMAT_DATE_BARRA, valor);//  FORMAT_DATE_QR_DOM
    }

    public static boolean equals(Date date1, Date date2, String format) {
        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 != null && date2 != null) {
            String date1Str = toString(format, date1);
            String date2Str = toString(format, date2);
            if (date1Str != null && date2Str != null) {
                return date1Str.equals(date2Str);
            }
            return false;
        }
        return false;
    }

    /**
     * Convierte Date a XMLGregorianCalendar
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {

        XMLGregorianCalendar xmlCalendar = null;
        try {
            if (date != null) {
                GregorianCalendar gCalendar = new GregorianCalendar();
                gCalendar.setTime(date);
                xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
            }
        } catch (DatatypeConfigurationException ex) {
            log.error("Error ", ex);
        }
        return xmlCalendar;
    }

    /**
     * Convierte DateString a XMLGregorianCalendar
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(String format, String valor) {
        return toXMLGregorianCalendar(DateUtil.toDate(format, valor));
    }

    /**
     * Convierte XMLGregorianCalendar a Date
     */
    public static Date toDate(XMLGregorianCalendar calendar) {
        return calendar == null ? null : calendar.toGregorianCalendar().getTime();
    }

    public static Date plus(Date date, int hoursToAdd, int minutesToAdd, int secondsToAdd) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.HOUR, hoursToAdd);
        calDateStart.add(Calendar.MINUTE, minutesToAdd);
        calDateStart.add(Calendar.SECOND, secondsToAdd);

        return calDateStart.getTime();
    }

    public static Date plus(Date date, int days) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.DAY_OF_MONTH, days);
        return calDateStart.getTime();
    }

    public static Date plusMilliseconds(Date date, int millisecondsToAdd) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.MILLISECOND, millisecondsToAdd);

        return calDateStart.getTime();
    }

    public static Date plusSeconds(Date date, int secondsToAdd) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.SECOND, secondsToAdd);

        return calDateStart.getTime();
    }

    public static Date plusMinutes(Date date, int minutesToAdd) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.MINUTE, minutesToAdd);

        return calDateStart.getTime();
    }

    public static Date defaultIsNull(Date date, Date defaultDate) {
        return date == null ? defaultDate : date;
    }

    public static long diferenciaDias(Date fechaInicial, Date fechaFin) {
        long diffInMillies = Math.abs(fechaFin.getTime() - fechaInicial.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long diferenciaDiasSinAbs(Date fechaInicial, Date fechaFin) {
        long diffInMillies = fechaFin.getTime() - fechaInicial.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long diferenciaHoras(Date fechaInicial, Date fechaFin) {
        long diffInMillies = Math.abs(fechaFin.getTime() - fechaInicial.getTime());
        return TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long diferenciaMinutos(Date fechaInicial, Date fechaFin) {
        long diffInMillies = Math.abs(fechaFin.getTime() - fechaInicial.getTime());
        return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Date addDays(Date date, int days) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.DATE, days);
        return calDateStart.getTime();
    }

    public static Date addYears(Date date, int anios) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(Calendar.YEAR, anios);
        return calDateStart.getTime();
    }

    public static Date plusField(Date date, int field, int amount) {
        Calendar calDateStart = Calendar.getInstance();
        calDateStart.setTime(date);
        calDateStart.add(field, amount);

        return calDateStart.getTime();
    }

    public static Date setYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static Date setCurrentYear(Date date) {
        return setYear(date, getYear(new Date()));
    }

    public static String formatDefault(Date valor) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_TIME);
            return sdf.format(valor);
        } catch (Exception ex) {
            log.error("Se genero una excepción al convertir {} al formato: {}", valor, FORMAT_DATE_TIME);
            return null;
        }
    }

    //region [comparation today & between]------------------------------------------------
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }

        Date startDate = formatToStart(new Date());
        Date endDate = formatToEnd(new Date());
        if (date.before(startDate) || date.after(endDate)) {
            return false;
        }
        return true;
    }

    public static boolean isBeforeToday(Date date) {
        if (date == null) {
            return false;
        }

        Date currentDate0000 = formatToStart(new Date());
        return date.before(currentDate0000);
    }

    public static boolean isAfterToday(Date date) {
        if (date == null) {
            return false;
        }

        Date currentDate2359 = formatToEnd(new Date());
        return date.after(currentDate2359);
    }

    public static boolean isDateAafterB(Date dateA, Date dateB, boolean permitEqual) {
        if (dateA == null || dateB == null) {
            return false;
        }

        Date dateB2359 = formatToEnd(dateB);
        if (permitEqual) {
            dateB2359 = addDays(dateB2359, -1);
        }

        return dateA.after(dateB2359);
    }

    public static boolean isDateAbeforeB(Date dateA, Date dateB, boolean permitEqual) {
        if (dateA == null || dateB == null) {
            return false;
        }

        Date dateB0000 = formatToStart(dateB);
        if (permitEqual) {
            dateB0000 = addDays(dateB0000, +1);
        }

        return dateA.before(dateB0000);
    }

    public static boolean dateTimeIsBetween(Date fecha, Date fechaInicio, Date fechaFin, String format) {
        if (fecha == null || fechaInicio == null || fechaFin == null) {
            return false;
        }
        if (equals(fecha, fechaInicio, format) || equals(fecha, fechaFin, format)) {
            return true;
        }
        return fecha.after(fechaInicio) && fecha.before(fechaFin);
    }

    public static boolean dateIsBetween(Date fecha, Date fechaInicio, Date fechaFin) {
        if (fecha == null || fechaInicio == null || fechaFin == null) {
            return false;
        }

        fecha = formatToStart(fecha);
        fechaInicio = formatToStart(fechaInicio);
        fechaFin = formatToStart(fechaFin);

        if (fechaInicio.compareTo(fechaFin) > 0) {
            return false;
        }

        if (fecha.compareTo(fechaInicio) < 0 || fecha.compareTo(fechaFin) > 0) {
            return false;
        }

        return true;
    }
    //endregion

    public static Date firstDateFromYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    //return enero=1 diciembre=12
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getLast2digitsYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year % 100;
    }

    public static int getDays(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int businessDays(int startDay, int endDay, int month, int year) {
        // Crear una instancia de Calendar para la fecha de inicio
        Calendar fechaInicio = Calendar.getInstance();
        fechaInicio.set(year, month - 1, startDay); // Mes es 0-indexado

        // Crear una instancia de Calendar para la fecha de fin
        Calendar fechaFin = Calendar.getInstance();
        fechaFin.set(year, month - 1, endDay); // Mes es 0-indexado

        // Calcular la diferencia en días
        long diferenciaMilisegundos = fechaFin.getTimeInMillis() - fechaInicio.getTimeInMillis();
        return (int) (diferenciaMilisegundos / (1000 * 60 * 60 * 24)); // Convertir milisegundos a días
    }

    public static int lastDayOfMonth(Short month, Short year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (lastDay == 31) {
            return 30;
        }
        if (month == 2 && lastDay == 29) {
            return 28;
        }
        return lastDay;
    }

    // calcula la cantidad de días validos, descartando los fines de semana y feriados


    public static Date convertToDateViaSqlTimestamp(LocalDateTime dateToConvert) {
        return dateToConvert != null ? java.sql.Timestamp.valueOf(dateToConvert) : null;
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


    public static String generateCronExpresion(String cronExpresion, Date fecha) {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        cronExpresion = cronExpresion.replace("{SECOND}", String.valueOf(c.get(Calendar.SECOND)))
                .replace("{MINUTE}", String.valueOf(c.get(Calendar.MINUTE)))
                .replace("{HOUR}", String.valueOf(c.get(Calendar.HOUR_OF_DAY)))
                .replace("{DAY}", String.valueOf(c.get(Calendar.DAY_OF_MONTH)))
                .replace("{MONTH}", obtenerAbrevMes(c.get(Calendar.MONTH)))
                .replace("{YEAR}", String.valueOf(c.get(Calendar.YEAR)));
        return cronExpresion;
    }

    private static String obtenerAbrevMes(int mes) {
        switch (mes) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
            default:
                return "";

        }
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static long calculateTimeDifference(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        Duration duration = Duration.between(start, end);
        return Math.abs(duration.toMinutes());
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Primer día del mes
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Último día del mes
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static Date getDateFirstDay(Integer year) {
        String formatStr = "yyyy-MM-dd HH:mm:ss";
        String dateStr = "" + year + "-01-01 00:00:00";
        return toDate(formatStr, dateStr);
    }

}
