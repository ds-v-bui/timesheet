package com.dsvn.starterkit.helpers.exporter;

import com.dsvn.starterkit.domains.models.filedata.HeaderConfig;
import com.dsvn.starterkit.utils.FieldUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class ExcelExporter<T> extends Exporter<T> {

    private String defaultSheetName = "Sheet1";

    private int headerRowStart = 0;

    @Override
    public InputStream download(List<T> obs) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (CollectionUtils.isEmpty(obs)) {
                return new ByteArrayInputStream(out.toByteArray());
            }

            List<HeaderConfig> headerConfigs = parseHeader(obs.get(0));

            if (CollectionUtils.isEmpty(headerConfigs)) {
                return new ByteArrayInputStream(out.toByteArray());
            }

            Sheet sheet = workbook.createSheet(defaultSheetName);

            // Header
            Row headerRow = sheet.createRow(headerRowStart);

            for (HeaderConfig headerConfig : headerConfigs) {
                Cell cell = headerRow.createCell(headerConfig.getPosition());
                cell.setCellValue(headerConfig.getName());
            }

            // Data
            int rowIdx = headerRowStart + 1;

            for (T ob : obs) {
                Row row = sheet.createRow(rowIdx++);

                for (HeaderConfig headerConfig : headerConfigs) {
                    Cell cell = row.createCell(headerConfig.getPosition());

                    Object value = FieldUtil.getFieldValue(headerConfig.getField(), ob);

                    cell.setCellValue(value != null ? value.toString() : null);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public Exporter<T> setHeaderRowStart(int headerRowStart) {
        this.headerRowStart = headerRowStart;
        return this;
    }
}
