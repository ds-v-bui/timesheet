package com.dsvn.starterkit.domains.models.mail;

import lombok.Getter;

@Getter
public enum MailTemplate {
    RESET_PASSWORD(MailFrom.NO_REPLY, "Reset password!", "reset-password.html");

    private final MailFrom from;
    private final String subject;
    private final String templateFile;

    private MailTemplate(MailFrom from, String subject, String templateFile) {
        this.from = from;
        this.subject = subject;
        this.templateFile = templateFile;
    }
}
