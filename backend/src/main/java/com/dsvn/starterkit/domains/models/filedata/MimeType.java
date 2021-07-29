package com.dsvn.starterkit.domains.models.filedata;

public enum MimeType {
    CSV("text/csv"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean equalValue(String mimeType) {
        return this.value.equals(mimeType);
    }
}
