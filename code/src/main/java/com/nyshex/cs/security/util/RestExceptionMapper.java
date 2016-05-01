/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nyshex.cs.security.util;

import static com.nyshex.cs.security.util.MessageBuilder.accessDenied;
import static com.nyshex.cs.security.util.MessageBuilder.authenticationRequired;
import static com.nyshex.cs.security.util.MessageBuilder.badRequest;

import javax.ejb.EJBException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.deltaspike.security.api.authorization.AccessDeniedException;
import org.picketlink.Identity;

/**
 * @author Pedro Igor
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

    @Inject
    private Instance<Identity> identityInstance;

    @Override
    public Response toResponse(final Throwable exception) {
        String message;
        if (EJBException.class.isInstance(exception)) {
            message = exception.getCause().getMessage();
        } else {
            message = exception.getMessage();
        }

        if (message == null) {
            message = "Unexpected error from server.";
        }

        MessageBuilder builder;

        if (AccessDeniedException.class.isInstance(exception)) {
            if (getIdentity().isLoggedIn()) {
                builder = accessDenied().message("Access Denied.");
            } else {
                builder = authenticationRequired();
            }
        } else {
            builder = badRequest();
        }

        return builder.message(message).build();
    }

    private Identity getIdentity() {
        return this.identityInstance.get();
    }
}
