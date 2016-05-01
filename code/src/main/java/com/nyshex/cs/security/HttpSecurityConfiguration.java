package com.nyshex.cs.security;

import javax.enterprise.event.Observes;

import org.picketlink.config.SecurityConfigurationBuilder;
import org.picketlink.event.SecurityConfigurationEvent;

/**
 * A simple CDI observer for the {@link org.picketlink.event.SecurityConfigurationEvent}. All the configuration related
 * to Http Security is provided from this bean.
 */
public class HttpSecurityConfiguration {

    /**
     * @param event
     *            The event is fired during application startup and allows you to provide any configuration to
     *            PicketLink before it is initialized.
     */
    public void onInit(@Observes final SecurityConfigurationEvent event) {
        final SecurityConfigurationBuilder builder = event.getBuilder();
        // @formatter:off
        builder
            .identity()
                .stateless()
            .http()
                .forPath("/rest/private/*")
                    .authenticateWith()
                        .token()
                    .cors()
                        .allowAll();
        // @formatter:on
    }

}
