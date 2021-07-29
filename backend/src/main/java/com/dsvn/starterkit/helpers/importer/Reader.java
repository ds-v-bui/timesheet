package com.dsvn.starterkit.helpers.importer;

import com.dsvn.starterkit.annotation.filedata.FileDataBindByName;
import com.dsvn.starterkit.annotation.filedata.FileDataBindByPosition;
import com.dsvn.starterkit.domains.models.filedata.HeaderConfig;
import com.dsvn.starterkit.domains.models.filedata.ImportRowError;
import com.dsvn.starterkit.domains.models.filedata.RowData;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

public abstract class Reader<T> {

    private final ModelMapper modelMapper = new ModelMapper();

    protected final List<ImportRowError> errors;

    protected final List<Integer> success;

    protected Class<T> cls;

    protected int headerLine;

    protected boolean isThrowExceptionIfMapFailed;

    public Reader(Class<T> cls) {
        this.errors = new ArrayList<>();
        this.success = new ArrayList<>();
        this.cls = cls;
        this.headerLine = 0;
    }

    public List<RowData<T>> exec(InputStream inputStream) {
        LinkedHashMap<Integer, LinkedHashMap<Integer, String>> data = readRawData(inputStream);

        if (!data.containsKey(headerLine)) {
            throw new RuntimeException("Header is empty!");
        }
        List<HeaderConfig> headerConfigs = this.parseHeader();
        Map<Integer, String> header = data.get(headerLine);

        List<RowData<T>> results = new ArrayList<>();
        for (Integer rowIdx : data.keySet()) {
            if (rowIdx <= headerLine) {
                continue;
            }

            Map<Integer, String> rowData = data.get(rowIdx);

            Map<String, String> objectMap = new HashMap<>();
            for (Integer colIdx : header.keySet()) {
                String headerName = header.get(colIdx);
                String fieldName = getFieldName(headerConfigs, colIdx, headerName);

                if (StringUtils.isEmpty(fieldName)) {
                    continue;
                }

                String rawValue = rowData.getOrDefault(colIdx, null);
                objectMap.put(fieldName, rawValue);
            }

            try {
                if (this.isEmptyRow(new ArrayList<>(objectMap.values()))) {
                    continue;
                }

                T obj = modelMapper.map(objectMap, cls);
                validate(obj);

                results.add(new RowData<>(rowIdx, obj));
                success.add(rowIdx);
            } catch (MappingException e) {
                if (isThrowExceptionIfMapFailed) {
                    throw new RuntimeException(e.getMessage());
                }

                errors.add(
                        ImportRowError.builder()
                                .row(rowIdx)
                                .trace(e.getCause().getCause().getMessage())
                                .build());
            } catch (ConstraintViolationException e) {
                if (isThrowExceptionIfMapFailed) {
                    throw new RuntimeException(e.getMessage());
                }

                Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
                List<String> listErrors =
                        violations.stream()
                                .map(v -> v.getInvalidValue().toString() + ": " + v.getMessage())
                                .collect(Collectors.toList());
                errors.add(
                        ImportRowError.builder()
                                .row(rowIdx)
                                .trace(String.join(", ", listErrors))
                                .build());
            }
        }

        return results;
    }

    public List<RowData<T>> exec(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);

        InputStream inputStream = new ByteArrayInputStream(bytes);

        return exec(inputStream);
    }

    public List<ImportRowError> getErrors() {
        return errors;
    }

    public List<Integer> getSuccess() {
        return success;
    }

    public Reader<T> setIsThrowExceptionIfMapFailed(boolean isThrowExceptionIfMapFailed) {
        this.isThrowExceptionIfMapFailed = isThrowExceptionIfMapFailed;
        return this;
    }

    public Reader<T> setHeaderLine(int headerLine) {
        this.headerLine = headerLine;
        return this;
    }

    private String getFieldName(List<HeaderConfig> headerConfigs, int colIdx, String headerName) {
        Optional<HeaderConfig> headerOptional =
                headerConfigs.stream()
                        .filter(
                                h ->
                                        h.getPosition() == colIdx
                                                || (StringUtils.hasLength(h.getName())
                                                        && h.getName().equals(headerName)))
                        .findFirst();

        return headerOptional.map(h -> h.getField().getName()).orElse(null);
    }

    private List<HeaderConfig> parseHeader() {
        return Arrays.stream(FieldUtils.getAllFields(cls))
                .map(
                        f -> {
                            FileDataBindByPosition fileDataBindByPosition =
                                    AnnotationUtils.findAnnotation(f, FileDataBindByPosition.class);
                            int position =
                                    fileDataBindByPosition != null
                                            ? fileDataBindByPosition.value()
                                            : -1;

                            FileDataBindByName fileDataBindByName =
                                    AnnotationUtils.findAnnotation(f, FileDataBindByName.class);
                            String name =
                                    fileDataBindByName != null
                                            ? fileDataBindByName.value()
                                            : f.getName();

                            return HeaderConfig.builder()
                                    .position(position)
                                    .name(name)
                                    .field(f)
                                    .build();
                        })
                .collect(Collectors.toList());
    }

    private void validate(T obj) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private boolean isEmptyRow(List<String> header) {
        return header.stream().allMatch(StringUtils::isEmpty);
    }

    protected abstract LinkedHashMap<Integer, LinkedHashMap<Integer, String>> readRawData(
            InputStream inputStream);
}
