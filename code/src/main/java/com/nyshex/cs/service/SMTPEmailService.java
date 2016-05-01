package com.nyshex.cs.service;

import java.util.Properties;

import javax.ejb.Singleton;
import javax.enterprise.inject.Alternative;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.nyshex.cs.model.EmailMessage;
import com.nyshex.cs.service.EmailService;

/**
 * A class for sending emails
 *
 */
@Singleton
@Alternative
public class SMTPEmailService implements EmailService {
    private static final String host = "localhost";

    /**
     * Send the email
     * @param emailMessage
     *
     * @throws MessagingException
     */
    @Override
    public void send(final EmailMessage emailMessage) throws MessagingException {
        final Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        final Session session = Session.getDefaultInstance(properties);
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailMessage.getSender()));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailMessage.getRecipient()));
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailMessage.getBccRecipient()));
        message.setSubject(emailMessage.getSubject());
        message.setContent(emailMessage.getContent(), "text/html");
        Transport.send(message);
    }
}

