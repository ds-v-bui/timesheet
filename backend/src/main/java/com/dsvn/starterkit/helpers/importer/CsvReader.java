package com.dsvn.starterkit.helpers.importer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CsvReader<T> extends Reader<T> {

    public CsvReader(Class<T> cls) {
        super(cls);
    }

    @Override
    protected LinkedHashMap<Integer, LinkedHashMap<Integer, String>> readRawData(
            InputStream inputStream) {
        LinkedHashMap<Integer, LinkedHashMap<Integer, String>> data = new LinkedHashMap<>();

        try (java.io.Reader reader = new InputStreamReader(inputStream);
                CSVReader csvReader = new CSVReader(reader)) {
            int rowIdx = 0;
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                List<String> rawRow = new ArrayList<>(Arrays.asList(line));
                LinkedHashMap<Integer, String> mapRow = new LinkedHashMap<>();
                for (int colIdx = 0; colIdx < rawRow.size(); colIdx++) {
                    mapRow.put(colIdx, rawRow.get(colIdx));
                }

                data.put(rowIdx, mapRow);
                rowIdx++;
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }

        return data;
    }
}
