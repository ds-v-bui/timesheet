package com.dsvn.starterkit.helpers.exporter;

import com.dsvn.starterkit.annotation.filedata.FileDataBindByName;
import com.dsvn.starterkit.annotation.filedata.FileDataBindByPosition;
import com.dsvn.starterkit.domains.models.filedata.HeaderConfig;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;

public abstract class Exporter<T> {

    public abstract InputStream download(List<T> obs);

    protected List<HeaderConfig> parseHeader(T bean) {
        List<Field> fields = FieldUtils.getAllFieldsList(bean.getClass());

        List<Field> fieldsPositionConfig =
                fields.stream()
                        .filter(
                                f ->
                                        f.isAnnotationPresent(FileDataBindByPosition.class)
                                                && f.getAnnotation(FileDataBindByPosition.class)
                                                                .value()
                                                        >= 0)
                        .collect(Collectors.toList());

        List<Field> fieldsNotConfig =
                fields.stream()
                        .filter(
                                f ->
                                        !f.isAnnotationPresent(FileDataBindByPosition.class)
                                                || f.getAnnotation(FileDataBindByPosition.class)
                                                                .value()
                                                        < 0)
                        .collect(Collectors.toList());

        Map<Integer, Field> fieldMap =
                fieldsPositionConfig.stream()
                        .collect(
                                Collectors.toMap(
                                        f -> f.getAnnotation(FileDataBindByPosition.class).value(),
                                        f -> f));

        int fieldsNotConfigIdx = 0;
        int fieldsNotConfigSize = fieldsNotConfig.size();
        int positionIdx = 0;
        while (fieldsNotConfigIdx < fieldsNotConfigSize) {
            if (fieldMap.containsKey(positionIdx)) {
                positionIdx++;
                continue;
            }

            fieldMap.put(positionIdx, fieldsNotConfig.get(fieldsNotConfigIdx));
            fieldsNotConfigIdx++;
        }

        return fieldMap.keySet().stream()
                .map(
                        position -> {
                            Field field = fieldMap.get(position);
                            String headerName =
                                    field.isAnnotationPresent(FileDataBindByName.class)
                                            ? field.getAnnotation(FileDataBindByName.class).value()
                                            : field.getName();

                            return HeaderConfig.builder()
                                    .position(position)
                                    .name(headerName)
                                    .field(field)
                                    .build();
                        })
                .sorted(Comparator.comparingInt(HeaderConfig::getPosition))
                .collect(Collectors.toList());
    }
}
