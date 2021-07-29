package com.dsvn.starterkit.domains.models.filedata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportRowError {
    private Integer row;
    private String trace;
}
