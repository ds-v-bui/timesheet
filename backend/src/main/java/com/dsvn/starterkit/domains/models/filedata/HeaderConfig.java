package com.dsvn.starterkit.domains.models.filedata;

import java.lang.reflect.Field;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HeaderConfig {
    int position;
    String name;
    Field field;
}
