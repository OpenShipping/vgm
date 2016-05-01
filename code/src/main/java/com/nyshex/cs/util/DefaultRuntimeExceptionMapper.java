package com.nyshex.cs.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Rethrow any RuntimeException, will return error to client and log
 */
@Provider
public class DefaultRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(final RuntimeException e) {
        throw e;
    }

}
