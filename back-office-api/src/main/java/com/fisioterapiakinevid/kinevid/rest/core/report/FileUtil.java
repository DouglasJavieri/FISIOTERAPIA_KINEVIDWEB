package com.fisioterapiakinevid.kinevid.rest.core.report;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;

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
public class FileUtil {

    public static void convertCsvToExcel(String csvFilePath,String excelFilePath,String separatorCsv){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(excelFilePath);
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("sheet1");
            String currentLine = null;
            int RowNum = 0;
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
            while ((currentLine = br.readLine()) != null) {
                String str[] = currentLine.split(separatorCsv);
                RowNum++;
                XSSFRow currentRow = sheet.createRow(RowNum);
                for (int i = 0; i < str.length; i++) {
                    currentRow.createCell(i).setCellValue(str[i]);
                }
            }
            br.close();
            workBook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            log.error("error", e);
        }

    }

    public static byte[] toByte(String pathFile) {
        try {
            Path path = FileUtil.getFileFromResources(pathFile).toPath();
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.error("error", e);
        }
        return null;
    }

    private static void printContent(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
            br.close();
        }

    }


    public static File getFileFromResources(String path) throws IOException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        return new File(path);
    }

    public static String byteToBase64(byte[] byteArray) {
        byte[] encoded = Base64.getEncoder().encode(byteArray);
        return new String(encoded);
    }

    public static byte[] base64ToByte(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static synchronized void updateFile(String ruta, byte[] contain) {
        try (Writer fw = new FileWriter(ruta, true)) {
            IOUtils.write(contain, fw, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String ruta) {
        try {
            Path path = Paths.get(ruta);
            if (path.toFile().exists()) {
                Files.delete(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compressFile(String ruta) {
        try (FileOutputStream fos = new FileOutputStream(ruta.replace("csv", "zip"))) {
            try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                File fileToZip = new File(ruta);
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

