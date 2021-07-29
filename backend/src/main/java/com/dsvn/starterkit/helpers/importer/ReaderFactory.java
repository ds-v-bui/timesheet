package com.dsvn.starterkit.helpers.importer;

import com.dsvn.starterkit.domains.models.filedata.MimeType;

public class ReaderFactory<T> {

    private final Class<T> cls;

    public ReaderFactory(Class<T> cls) {
        this.cls = cls;
    }

    public Reader<T> getReader(String mimeType) {
        if (MimeType.CSV.equalValue(mimeType)) {
            return new CsvReader<>(cls);
        }

        if (MimeType.EXCEL.equalValue(mimeType)) {
            return new ExcelReader<>(cls);
        }

        throw new RuntimeException("No file extension support");
    }
}
