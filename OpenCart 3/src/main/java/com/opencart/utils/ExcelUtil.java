package com.opencart.utils;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static List<String[]> leerDatos(String ruta, String hoja) throws Exception {
        List<String[]> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(new File(ruta));
        Workbook wb = WorkbookFactory.create(fis);
        
        Sheet sheet = wb.getSheet(hoja);
        if (sheet == null) {
            throw new RuntimeException("La hoja '" + hoja + "' no existe en el archivo: " + ruta);
        }

        for (Row row : sheet) {
            String[] fila = new String[row.getLastCellNum()];
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                fila[i] = (cell != null) ? cell.toString() : "";
            }
            data.add(fila);
        }
        wb.close();
        fis.close();
        return data;
    }

    public static void escribirLog(String ruta, String hoja, String[] valores) throws Exception {
        File file = new File(ruta);

        FileInputStream fis = new FileInputStream(file);
        Workbook wb = WorkbookFactory.create(fis);
        Sheet sheet = wb.getSheet(hoja);
        if (sheet == null) {
            sheet = wb.createSheet(hoja);
        }

        // Buscar la primera fila completamente en blanco (null o todas las celdas vacías).
        // Si no se encuentra ninguna, escribir en la fila siguiente a la última.
        int lastRowNum = sheet.getLastRowNum();
        int newRowIndex = -1;

        for (int i = 0; i <= lastRowNum; i++) {
            Row existing = sheet.getRow(i);

            if (existing == null) {
                newRowIndex = i;
                break;
            }

            boolean allBlank = true;
            short lastCellNum = existing.getLastCellNum();
            if (lastCellNum < 0) {
                // fila sin celdas creadas
                newRowIndex = i;
                break;
            }

            for (int c = 0; c < lastCellNum; c++) {
                Cell cell = existing.getCell(c);
                if (cell != null && cell.getCellType() != CellType.BLANK &&
                        cell.toString() != null && !cell.toString().trim().isEmpty()) {
                    allBlank = false;
                    break;
                }
            }

            if (allBlank) {
                newRowIndex = i;
                break;
            }
        }

        if (newRowIndex == -1) {
            newRowIndex = lastRowNum + 1;
        }

        Row row = sheet.createRow(newRowIndex);

        for (int i = 0; i < valores.length; i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(valores[i] == null ? "" : valores[i]);
        }

        fis.close();

        FileOutputStream fos = new FileOutputStream(file);
        wb.write(fos);
        fos.close();
        wb.close();
    }
}
