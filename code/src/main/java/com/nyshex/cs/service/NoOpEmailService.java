package com.nyshex.cs.service;

import com.nyshex.cs.model.EmailMessage;

import javax.ejb.Singleton;
import javax.enterprise.inject.Alternative;


/**
 * Abstract class for sending e-mails with no options.
 */
@Alternative
@Singleton
public class NoOpEmailService implements EmailService {
    @Override
    public void send(final EmailMessage emailMessage) {
        /* nada! */
    }
}
