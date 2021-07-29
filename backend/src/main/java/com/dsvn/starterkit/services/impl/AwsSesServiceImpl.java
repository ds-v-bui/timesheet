package com.dsvn.starterkit.services.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.dsvn.starterkit.domains.models.mail.Mail;
import com.dsvn.starterkit.domains.models.mail.MailAttachment;
import com.dsvn.starterkit.services.MailService;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class AwsSesServiceImpl implements MailService {

    @Autowired private AmazonSimpleEmailService simpleEmailService;

    @Override
    public void sendMail(Mail mail) {
        try {
            if (CollectionUtils.isEmpty(mail.getFiles())) {
                // Set to address
                Destination destination = new Destination().withToAddresses(mail.getTo());

                // Set cc addresses
                if (!CollectionUtils.isEmpty(mail.getCc())) {
                    destination.withCcAddresses(mail.getCc());
                }

                // Set bcc addresses
                if (!CollectionUtils.isEmpty(mail.getBcc())) {
                    destination.withBccAddresses(mail.getBcc());
                }

                Content subject = new Content().withData(mail.getSubject());
                Content textBody = new Content().withData(mail.getBody());

                Body body =
                        mail.isHtml()
                                ? new Body().withHtml(textBody)
                                : new Body().withText(textBody);

                Message message = new Message().withSubject(subject).withBody(body);

                SendEmailRequest request =
                        new SendEmailRequest()
                                .withSource(mail.getFrom())
                                .withReplyToAddresses(mail.getFrom())
                                .withDestination(destination)
                                .withMessage(message);

                // Send the email.
                simpleEmailService.sendEmail(request);
            } else {

                Session session = Session.getDefaultInstance(new Properties());

                // Create a new MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // set subject, from
                message.setSubject(mail.getSubject(), "UTF-8");
                message.setFrom(new InternetAddress(mail.getFrom()));
                message.setReplyTo(new Address[] {new InternetAddress(mail.getFrom())});

                // set to address
                Address[] addresses = new Address[mail.getTo().size()];
                for (int i = 0; i < mail.getTo().size(); i++) {
                    addresses[i] = new InternetAddress(mail.getTo().get(i));
                }
                message.setRecipients(javax.mail.Message.RecipientType.TO, addresses);

                // Set cc addresses
                if (!CollectionUtils.isEmpty(mail.getCc())) {
                    addresses = new Address[mail.getCc().size()];
                    for (int i = 0; i < mail.getCc().size(); i++) {
                        addresses[i] = new InternetAddress(mail.getCc().get(i));
                    }
                    message.setRecipients(javax.mail.Message.RecipientType.CC, addresses);
                }

                // Set cc addresses
                if (!CollectionUtils.isEmpty(mail.getBcc())) {
                    addresses = new Address[mail.getBcc().size()];
                    for (int i = 0; i < mail.getBcc().size(); i++) {
                        addresses[i] = new InternetAddress(mail.getBcc().get(i));
                    }
                    message.setRecipients(javax.mail.Message.RecipientType.BCC, addresses);
                }

                // Add a MIME part to the message for body
                MimeMultipart mp = new MimeMultipart();
                BodyPart part = new MimeBodyPart();
                if (mail.isHtml()) {
                    part.setContent(mail.getBody(), "text/html; charset=UTF-8");
                } else {
                    part.setText(mail.getBody());
                }
                mp.addBodyPart(part);

                // set message contents
                message.setContent(mp);

                // Add attachments part of message
                for (MailAttachment file : mail.getFiles()) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    DataSource ds =
                            new ByteArrayDataSource(file.getContent(), file.getContentType());
                    attachment.setDataHandler(new DataHandler(ds));
                    attachment.setHeader("Content-ID", "<" + UUID.randomUUID().toString() + ">");
                    attachment.setFileName(file.getName());
                    mp.addBodyPart(attachment);
                }

                // Send the email
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                message.writeTo(outputStream);
                RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

                SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
                simpleEmailService.sendRawEmail(rawEmailRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
