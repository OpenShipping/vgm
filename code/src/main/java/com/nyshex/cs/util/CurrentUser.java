package com.nyshex.cs.util;

import com.nyshex.cs.data.UserRepository;
import com.nyshex.cs.model.User;

import lombok.AllArgsConstructor;

/**
 * The current user
 */
@AllArgsConstructor
public class CurrentUser {

    private final int userId;

    private final boolean administratorRole;

    private final UserRepository userRepository;

    /**
     * @return the current user
     */
    public User findUser() {
        return userRepository.findUser(userId);
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the administratorRole
     */
    public boolean hasAdministratorRole() {
        return administratorRole;
    }

}
