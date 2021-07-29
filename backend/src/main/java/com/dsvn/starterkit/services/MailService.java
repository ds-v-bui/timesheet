package com.dsvn.starterkit.services;

import com.dsvn.starterkit.domains.models.mail.Mail;

public interface MailService {

    void sendMail(Mail mail);
}
