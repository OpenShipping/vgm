package com.nyshex.cs.service;

import com.nyshex.cs.model.EmailMessage;

import javax.ejb.Singleton;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import java.util.logging.Logger;


/**
 * Logger for all e-mails
 */
@Singleton
@Alternative
public class LogEmailService implements EmailService {
    @Inject
    private Logger logger;

    @Override
    public void send(final EmailMessage emailMessage) {
        final String logMessage = new StringBuilder()
            .append("To: ").append(emailMessage.getRecipient()).append('\n')
            .append("From: ").append(emailMessage.getSender()).append('\n')
            .append("Bcc: ").append(emailMessage.getBccRecipient()).append('\n')
            .append("Subject: ").append(emailMessage.getSubject()).append("\n\n")
            .append(emailMessage.getContent()).append('\n')
            .toString();
        logger.info(logMessage);
    }
}
