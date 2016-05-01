package com.nyshex.cs.util;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;

import com.nyshex.cs.data.RepositoryHelper;

/**
 * Intercept @Transactional and SET the @user variable in the current session
 */
@Transactional
@Interceptor
public class TransactionalInterceptor {

    @Inject
    private RepositoryHelper repositoryHelper;

    @Inject
    private CurrentUser currentUser;

    /**
     * @param ctx
     * @return null
     * @throws Exception
     */
    @AroundInvoke
    public Object aroundInvoke(final InvocationContext ctx) throws Exception {
        repositoryHelper.setUser(currentUser.getUserId());
        return ctx.proceed();
    }

}
