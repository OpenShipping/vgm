package com.nyshex.cs.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Rethrow any Exception wrapped in RuntimeException, will return error to client and log
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(final Exception e) {
        throw new RuntimeException(e);
    }

}
