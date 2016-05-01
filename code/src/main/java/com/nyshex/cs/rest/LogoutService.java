package com.nyshex.cs.rest;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.picketlink.Identity;
import org.picketlink.authorization.annotations.LoggedIn;
import org.picketlink.idm.credential.Token;
import org.picketlink.idm.model.Account;

import com.nyshex.cs.security.authentication.JWSToken;
import com.nyshex.cs.security.model.MyUser;

/**
 * Service for log out
 */
@Path("/private/logout")
public class LogoutService {

    @Inject
    private Token.Provider<JWSToken> tokenProvider;

    @Inject
    private Identity identity;

    @Inject
    private Logger logger;

    /**
     * Logout
     * <p>
     * Not used with GET as that will cause issues with prefetch.
     */
    @POST
    @LoggedIn
    public void logout() {
        final MyUser myUser = (MyUser) identity.getAccount();
        logger.info(String.format(
                "[/private/logout] Logging out MyUser.loginName %s", myUser.getLoginName()));

        final Account account = identity.getAccount();
        tokenProvider.invalidate(account);
        identity.logout();
    }

}
