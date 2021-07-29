package com.dsvn.starterkit.domains.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class Base64Form {
    @NotNull @NotBlank String dataUri;

    String fileName;
}
