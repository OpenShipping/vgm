package com.nyshex.cs.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.nyshex.cs.model.User;
import com.nyshex.cs.rest.dto.UserParamsDTO;
import com.nyshex.cs.security.ApplicationRole;
import com.nyshex.cs.security.model.MyUser;

import org.picketlink.authorization.annotations.LoggedIn;
import org.picketlink.authorization.annotations.RolesAllowed;

import com.nyshex.cs.util.CurrentUser;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.query.IdentityQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * A rest service for handling the identity of the logged-in user
 */
@Path("/private/identity")
@Stateless
@LoggedIn
@RolesAllowed({ApplicationRole.USER, ApplicationRole.ADMINISTRATOR})
public class IdentityRestService {

    @Inject
    private PartitionManager partitionManager;

    @Inject
    private CurrentUser currentUser;

    @Inject
    private Logger logger;


    /**
     * @return the login name of the active user
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserParamsDTO getProfile() {
        final User user = currentUser.findUser();
        final UserParamsDTO userParamsDTO = new UserParamsDTO();
        userParamsDTO.setFirstName(user.getFirstName());
        userParamsDTO.setLastName(user.getLastName());
        userParamsDTO.setEmail(user.getEmail());
        userParamsDTO.setBusinessName(user.getBusiness().getName());
        userParamsDTO.setBusinessType(user.getBusiness().getType());
        userParamsDTO.setBusinessAddress(user.getBusiness().getAddress());
        userParamsDTO.setBusinessCity(user.getBusiness().getCity());
        userParamsDTO.setBusinessPostalCode(user.getBusiness().getPostalCode());
        userParamsDTO.setBusinessCountry(user.getBusiness().getCountry());
        logger.info(String.format("[/private/identity] Information requested by %s %s, user id %d.",
                user.getFirstName(), user.getLastName(), user.getId()));
        return userParamsDTO;
    }

    /**
     * @param oldPassword
     * @param newPassword
     * @return true, that user has been updated
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> updatePassword(@QueryParam("oldPassword") final String oldPassword,
            @QueryParam("newPassword") final String newPassword) {
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final IdentityQueryBuilder queryBuilder = identityManager.getQueryBuilder();
        final List<MyUser> myUsers = queryBuilder.createIdentityQuery(MyUser.class)
            .where(queryBuilder.equal(MyUser.USER_NAME, currentUser.findUser().getEmail()))
            .getResultList();

        Map<String, Integer> response = new HashMap<>();
        if (myUsers == null || myUsers.size() != 1) {
            logger.severe(String
                    .format("[/private/identity] Multiple loginNames found for email %s",
                            currentUser.findUser().getEmail()));
            response.put("multipleUsersFound", 1);
            return response;
        }

        final MyUser myUser = myUsers.get(0);

        // Check current password is correct
        UsernamePasswordCredentials oldCredential = new UsernamePasswordCredentials();
        oldCredential.setPassword(new Password(oldPassword));
        oldCredential.setUsername(myUser.getLoginName());
        identityManager.validateCredentials(oldCredential);

        if (Credentials.Status.VALID.equals(oldCredential.getStatus())) {
            BasicModel.getUser(identityManager, myUser.getLoginName());
            identityManager.updateCredential(myUser, new Password(newPassword));
            logger.info(String.format("[/private/identity] Password updated for %s %s.",
                    myUser.getLoginName(), myUser.getId()));
            response.put("updated", 1);
            return response;
        } else {
            logger.info(String.format("[/private/identity] Invalid password provided by loginName %s",
                    myUser.getLoginName(), myUser.getId()));
            response.put("invalidOldPassword", 1);
            return response;
        }
    }

}
