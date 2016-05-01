package com.nyshex.cs.util;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Set cache headers to no cache on /rest resources and to 1 hour on the other (static) resources.
 */
public class CacheControlFilter implements Filter {

    @Inject
    private Logger log;

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String servletPath = request.getServletPath();
        final String cacheControl;
        if ("/rest".equals(servletPath)) {
            cacheControl = "private, max-age=0, no-cache";
        } else {
            cacheControl = "public, max-age=3600";
        }
        response.setHeader("Cache-Control", cacheControl);

        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        log.fine("init()");
    }

    @Override
    public void destroy() {
        log.fine("destroy()");
    }

}
