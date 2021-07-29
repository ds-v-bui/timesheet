package com.dsvn.starterkit.api.responses;

import java.io.InputStream;
import java.util.List;
import lombok.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
    public static ResponseEntity<?> ofResource(Object data) {
        return new ResponseEntity<>(new DataPayload("OK", data), HttpStatus.OK);
    }

    public static ResponseEntity<?> ofCreated(Object data) {
        return new ResponseEntity<>(new DataPayload("Created!", data), HttpStatus.CREATED);
    }

    public static ResponseEntity<?> ofNoContent() {
        return new ResponseEntity<>(new DataPayload("No content!", null), HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<?> ofDownloadFile(InputStream in, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    public static ResponseEntity<?> ofErrorResponse(
            @NonNull HttpStatus status, String message, Integer errorCode) {
        return new ResponseEntity<>(new ErrorPayload(errorCode, message, null), status);
    }

    public static ResponseEntity<?> ofErrorResponse(
            @NonNull HttpStatus status,
            List<ErrorPayload.Error> errors,
            String message,
            Integer errorCode) {
        return new ResponseEntity<>(new ErrorPayload(errorCode, message, errors), status);
    }
}
