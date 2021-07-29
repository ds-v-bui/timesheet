package com.dsvn.starterkit.domains.models.filedata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RowData<T> {
    private Integer row;
    private T data;
}
