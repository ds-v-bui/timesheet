package com.dsvn.starterkit.helpers.exporter;

import com.dsvn.starterkit.domains.models.filedata.HeaderConfig;
import com.dsvn.starterkit.utils.FieldUtil;
import com.opencsv.CSVWriter;
import java.io.*;
import java.util.List;
import org.springframework.util.CollectionUtils;

public class CsvExporter<T> extends Exporter<T> {

    @Override
    public InputStream download(List<T> obs) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                OutputStreamWriter streamWriter = new OutputStreamWriter(out);
                CSVWriter csvWriter = new CSVWriter(streamWriter)) {

            if (CollectionUtils.isEmpty(obs)) {
                return new ByteArrayInputStream(out.toByteArray());
            }

            List<HeaderConfig> headerConfigs = parseHeader(obs.get(0));

            if (CollectionUtils.isEmpty(headerConfigs)) {
                return new ByteArrayInputStream(out.toByteArray());
            }

            int headerSize = headerConfigs.size();

            // Header
            String[] header = new String[headerConfigs.get(headerSize - 1).getPosition() + 1];
            for (HeaderConfig headerConfig : headerConfigs) {
                header[headerConfig.getPosition()] = headerConfig.getName();
            }
            csvWriter.writeNext(header);

            // Data
            for (T ob : obs) {
                String[] itemsArray =
                        new String[headerConfigs.get(headerSize - 1).getPosition() + 1];
                for (HeaderConfig headerConfig : headerConfigs) {
                    Object rawValue = FieldUtil.getFieldValue(headerConfig.getField(), ob);
                    String sValue = rawValue != null ? rawValue.toString() : null;

                    itemsArray[headerConfig.getPosition()] = sValue;
                }

                csvWriter.writeNext(itemsArray);
            }

            streamWriter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
