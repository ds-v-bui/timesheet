package com.dsvn.starterkit.domains.models.mail;

import com.dsvn.starterkit.utils.ResourceReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.text.StringSubstitutor;

@Getter
@Setter
public class Mail {

    private static final String TEMPLATE_RESOURCE_PATH = "template/mail";

    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private MailFrom from;
    private String subject;
    private String body;
    private boolean html;
    private List<MailAttachment> files;

    public Mail() {
        this.to = new ArrayList<>();
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();
        this.from = MailFrom.NO_REPLY;
        this.files = new ArrayList<>();
        this.html = true;
    }

    public Mail(MailTemplate mailTemplate, Map<String, String> templateData) {
        this();

        this.from = mailTemplate.getFrom();
        this.subject = mailTemplate.getSubject();

        String templateStr =
                ResourceReader.readFileToString(
                        TEMPLATE_RESOURCE_PATH + "/" + mailTemplate.getTemplateFile());
        StringSubstitutor subst = new StringSubstitutor(templateData);
        this.body = subst.replace(templateStr);
        this.html = true;
    }

    public Mail setTo(String... toAddresses) {
        Collections.addAll(this.to, toAddresses);
        return this;
    }

    public Mail setCC(String... ccAddresses) {
        Collections.addAll(this.cc, ccAddresses);
        return this;
    }

    public Mail setBcc(String... bccAddresses) {
        Collections.addAll(this.bcc, bccAddresses);
        return this;
    }

    public String getFrom() {
        return String.format("\"%s\" <%s>", from.getName(), from.getEmail());
    }
}
