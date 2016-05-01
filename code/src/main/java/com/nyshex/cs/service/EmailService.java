package com.nyshex.cs.service;

import com.nyshex.cs.model.EmailMessage;

import javax.mail.MessagingException;


/**
 * Layer for abstracting the Email Service for different
 * implementations.
 */
public interface EmailService {
    /**
     * @param emailMessage
     * @throws MessagingException
     */
    void send(final EmailMessage emailMessage) throws MessagingException;
}
