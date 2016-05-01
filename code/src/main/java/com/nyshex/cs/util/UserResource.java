package com.nyshex.cs.util;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Role;

import com.nyshex.cs.data.UserRepository;
import com.nyshex.cs.model.User;
import com.nyshex.cs.security.ApplicationRole;
import com.nyshex.cs.security.model.MyUser;

/**
 * Inject User found using picketlink
 */
public class UserResource {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RelationshipManager relationshipManager;

    private final Role administrator;

    @Inject
    UserResource(final IdentityManager identityManager) {
        Role administrator2;
        try {
            administrator2 = BasicModel.getRole(identityManager, ApplicationRole.ADMINISTRATOR);
        } catch (final ContextNotActiveException e) {
            administrator2 = null;
        }
        administrator = administrator2;
    }

    /**
     * @param identity
     * @return the active user
     */
    @Produces
    public CurrentUser produceCurrentUser(final Identity identity) {
        int userId;
        boolean administratorRole;
        try {
            final MyUser myUser = (MyUser) identity.getAccount();
            userId = myUser.getUser().getId();
            administratorRole = BasicModel.hasRole(relationshipManager, myUser, administrator);
        } catch (final ContextNotActiveException e) {
            userId = -3; // TODO create service user in database...
            administratorRole = false;
        }
        return new CurrentUser(userId, administratorRole, userRepository);
    }

    /**
     * @param identity
     * @return the active user
     */
    @Produces
    public MyUser produceMyUser(final Identity identity) {
        return (MyUser) identity.getAccount();
    }

    /**
     * @param myUser
     * @return the active user
     */
    @Produces
    public User produceUser(final MyUser myUser) {
        return myUser.getUser();
    }

}
