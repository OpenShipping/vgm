package com.nyshex.cs.security.model;

import static org.picketlink.idm.model.annotation.IdentityStereotype.Stereotype.USER;
import static org.picketlink.idm.model.annotation.StereotypeProperty.Property.IDENTITY_USER_NAME;

import javax.enterprise.inject.Vetoed;

import org.picketlink.idm.model.AbstractIdentityType;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.annotation.AttributeProperty;
import org.picketlink.idm.model.annotation.IdentityStereotype;
import org.picketlink.idm.model.annotation.StereotypeProperty;
import org.picketlink.idm.model.annotation.Unique;
import org.picketlink.idm.query.AttributeParameter;
import org.picketlink.idm.query.QueryParameter;

import com.nyshex.cs.model.User;

import lombok.Getter;
import lombok.Setter;

/**
 * User for picketlink
 */
@IdentityStereotype(USER)
@Vetoed
@Getter
@Setter
public class MyUser extends AbstractIdentityType implements Account {


    /**
     * <p>Can be used to query users by their activation code.</p>
     */
    public static final AttributeParameter ACTIVATION_CODE = QUERY_ATTRIBUTE.byName("activationCode");

    /**
     * Can be used to query users by their login name.
     */
    public static final QueryParameter USER_NAME = QUERY_ATTRIBUTE.byName("loginName");

    @StereotypeProperty(IDENTITY_USER_NAME)
    @AttributeProperty
    @Unique
    private String loginName;

    @AttributeProperty
    private User user;

    @AttributeProperty
    private String activationCode;

    /**
     * Setter for activation code to make null
     */
    public void invalidateActivationCode() {
        this.activationCode = null;
    }
}
