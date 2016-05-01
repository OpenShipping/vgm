package com.nyshex.cs.security;

import javax.enterprise.event.Observes;

import org.picketlink.config.SecurityConfigurationBuilder;
import org.picketlink.event.SecurityConfigurationEvent;

import com.nyshex.cs.security.model.MyUser;

/**
 * A simple CDI observer for the {@link org.picketlink.event.SecurityConfigurationEvent}. All the configuration to
 * PicketLink Identity Management is provided from this bean.
 */
public class IdentityManagementConfiguration {

    /**
     * @param event
     *            The event is fired during application startup and allows you to provide any configuration to
     *            PicketLink before it is initialized.
     */
    @SuppressWarnings("unchecked")
    public void configureIdentityManagement(@Observes final SecurityConfigurationEvent event) {
        final SecurityConfigurationBuilder builder = event.getBuilder();
        // @formatter:off
        builder
            .idmConfig()
                .named("default.config")
                    .stores()
                        .jpa()
                            .supportType(MyUser.class)
                            .supportAllFeatures();
        // @formatter:on
    }

}
