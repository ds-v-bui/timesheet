package com.dsvn.starterkit.helpers.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader<T> extends Reader<T> {

    public ExcelReader(Class<T> cls) {
        super(cls);
    }

    @Override
    protected LinkedHashMap<Integer, LinkedHashMap<Integer, String>> readRawData(
            InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter dataFormatter = new DataFormatter();
            LinkedHashMap<Integer, LinkedHashMap<Integer, String>> data = new LinkedHashMap<>();
            for (Row row : sheet) {
                data.put(row.getRowNum(), new LinkedHashMap<>());
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    data.get(row.getRowNum()).put(cell.getColumnIndex(), cellValue);
                }
            }

            return data;
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
