package com.nyshex.cs.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.nyshex.cs.data.BusinessRepository;
import com.nyshex.cs.data.RepositoryHelper;
import com.nyshex.cs.data.UserRepository;
import com.nyshex.cs.model.Business;
import com.nyshex.cs.model.EmailMessage;
import com.nyshex.cs.model.User;
import com.nyshex.cs.rest.dto.UserParamsDTO;
import com.nyshex.cs.security.ApplicationRole;
import com.nyshex.cs.security.model.MyUser;
import com.nyshex.cs.service.EmailService;
import com.nyshex.cs.util.Settings;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.query.IdentityQueryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * A rest service for handling the identity of the logged-in user
 */
@Path("/registration")
@Stateless
public class RegistrationRestService {

    @Inject
    private PartitionManager partitionManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private BusinessRepository businessRepository;

    @Inject
    private Logger logger;

    @Inject
    private RepositoryHelper repositoryHelper;

    @Inject
    private EmailService emailService;

    @Inject
    private Settings settings;

    /**
     * @param params
     * @return Key name indicating success or failure.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> register(@QueryParam("registrationParams") final UserParamsDTO params) {

        // Check there are not already e-mails in database
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final IdentityQueryBuilder queryBuilder = identityManager.getQueryBuilder();
        final List<MyUser> myUsers = queryBuilder.createIdentityQuery(MyUser.class)
                .where(queryBuilder.equal(MyUser.USER_NAME, params.getEmail()))
                .getResultList();
        if (userRepository.findUsersByEmail(params.getEmail()).size() > 0 ||
                myUsers.size() > 0) {
            logger.warning(String
                    .format("[/registration] User tried to register with existing email %s", params.getEmail()));
            return Collections.singletonMap("emailAlreadyExists", 1);
        }

        // Persist Business, User and Grant Basic Role
        final String peristanceLogMessage = String
                .format("[/registration] New registration info saved for %s %s of %s",
                params.getFirstName(), params.getLastName(), params.getBusinessName());
        repositoryHelper.setLog(peristanceLogMessage);
        repositoryHelper.setUser(-1);
        final Business business = new Business();
        business.setName(params.getBusinessName());
        business.setType(params.getBusinessType());
        business.setAddress(params.getBusinessAddress());
        business.setCity(params.getBusinessCity());
        business.setPostalCode(params.getBusinessPostalCode());
        business.setCountry(params.getBusinessCountry());
        businessRepository.persist(business);
        final User user = new User();
        user.setBusiness(business);
        user.setFirstName(params.getFirstName());
        user.setLastName(params.getLastName());
        user.setEmail(params.getEmail());
        userRepository.persist(user);
        final MyUser myUser = new MyUser();
        myUser.setUser(user);
        myUser.setLoginName(user.getEmail());
        myUser.setEnabled(false); // Disable account
        final String activationCode = UUID.randomUUID().toString();
        myUser.setActivationCode(activationCode); // we set an activation code for future use.
        identityManager.add(myUser);
        identityManager.updateCredential(myUser, new Password(params.getPassword()));
        Role basicUser = BasicModel.getRole(identityManager, ApplicationRole.USER);
        BasicModel.grantRole(
                partitionManager.createRelationshipManager(),
                myUser,
                basicUser);
        logger.info(peristanceLogMessage);

        // Email link for activation
        final EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSender("no-reply-vgms@nyshex.com");
        emailMessage.setBccRecipient("notifications@nyshex.com");
        emailMessage.setSubject("Activate your account");
        emailMessage.setRecipient(user.getEmail());
        final String env = settings.getEnvironment().name().toLowerCase().replace("_", "-");
        emailMessage.setContent(String.format("\n"
                + "To activate your account, please go to the following link: \n"
                + "https://%s.nyshex.com/#/activate/%s",
                env, activationCode));
        try {
            emailService.send(emailMessage);
            logger.info(String
                    .format("[/registration] Emailing activation link to MyUser %s id %s",
                            myUser.getLoginName(), myUser.getId()));
        } catch (final MessagingException mex) {
            final String failureLog = String
                    .format("[/registration] Failed to send email to MyUser %s id %s",
                            myUser.getLoginName(), myUser.getId());
            logger.log(Level.SEVERE, failureLog, mex);
        }
        return Collections.singletonMap("registered", 1);

    }

    /**
     * @param activationCode
     * @return Key name indicating success or failure.
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> activate(@QueryParam("activationCode") String activationCode) {
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final IdentityQueryBuilder queryBuilder = identityManager.getQueryBuilder();
        final List<MyUser> myUsers = queryBuilder.createIdentityQuery(MyUser.class)
            .where(queryBuilder.equal(MyUser.ACTIVATION_CODE, activationCode))
            .getResultList();
        if (myUsers.isEmpty()) {
            logger.info(String
                    .format("[/registration] Activation code not found: %s",
                            activationCode));
            return Collections.singletonMap("activationCodeNotFound", 1);
        }
        MyUser myUser = myUsers.get(0);
        myUser.setEnabled(true);
        myUser.invalidateActivationCode();
        identityManager.update(myUser);
        logger.info(String
                .format("[/registration] Activated user %s %s",
                        myUser.getLoginName(), myUser.getId()));
        return Collections.singletonMap("activated", 1);
    }

}
