package com.nyshex.cs.model;

import lombok.Getter;
import lombok.Setter;


/**
 * Helper class for transitively storing email message fields.
 */
@Getter
@Setter
public class EmailMessage {
    private String recipient;
    private String bccRecipient;
    private String sender;
    private String subject;
    private String content;
}
