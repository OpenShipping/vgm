package com.nyshex.cs.service;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.nyshex.cs.model.EmailMessage;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;


/**
 * Helper class for sending email via AWS API
 */
@Singleton
public class AWSSimpleEmailService implements EmailService {
    private final AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

    /**
     * For adding any post configuration like specifying region.
     */
    @PostConstruct
    public void postConstruct() {
        final Region REGION = Region.getRegion(Regions.US_WEST_2);
        client.setRegion(REGION);
    }

    @Override
    public void send(final EmailMessage emailMessage) {
        client.sendEmail(buildAwsMessage(emailMessage));
    }

    private SendEmailRequest buildAwsMessage(final EmailMessage emailMessage) {
        final Destination destination = new Destination().withToAddresses(emailMessage.getRecipient());
        final Content subject = new Content().withData(emailMessage.getSubject());
        final Content textBody = new Content().withData(emailMessage.getContent());
        final Body body = new Body().withText(textBody);
        final Message message = new Message()
            .withSubject(subject)
            .withBody(body);
        final SendEmailRequest sendEmailRequest = new SendEmailRequest()
            .withSource(emailMessage.getSender())
            .withDestination(destination)
            .withMessage(message);
        return sendEmailRequest;
    }
}
