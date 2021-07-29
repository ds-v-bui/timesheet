package com.dsvn.starterkit.domains.models.mail;

import lombok.Data;

@Data
public class MailAttachment {
    private String name;
    private byte[] content;
    private String contentType;
}
