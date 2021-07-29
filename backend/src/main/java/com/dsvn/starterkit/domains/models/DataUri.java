package com.dsvn.starterkit.domains.models;

import com.dsvn.starterkit.exceptions.DataUriStringException;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataUri {
    private String mimeType;
    private String base64;

    public DataUri(String dataUri) {
        validateFormat(dataUri);

        this.mimeType = getMimeType(dataUri);
        this.base64 = getEncodedString(dataUri);
    }

    private void validateFormat(String dataUri) {
        if (!dataUri.startsWith("data:")) {
            throw new DataUriStringException("Not DataUri format");
        }

        if (!dataUri.contains(";base64,")) {
            throw new DataUriStringException("Not Base64 encoded DataUri string");
        }

        String encodedString = getEncodedString(dataUri);
        try {
            Base64.getDecoder().decode(encodedString);
        } catch (Exception e) {
            throw new DataUriStringException("Not Base64 encoded string");
        }
    }

    private String getMimeType(String dataUri) {
        return dataUri.substring("data:".length(), dataUri.indexOf(";base64,"));
    }

    private String getEncodedString(String dataUri) {
        return dataUri.split(",")[1];
    }
}
