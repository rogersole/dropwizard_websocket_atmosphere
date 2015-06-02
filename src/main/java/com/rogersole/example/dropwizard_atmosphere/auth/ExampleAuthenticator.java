package com.rogersole.example.dropwizard_atmosphere.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.core.User;
import com.rogersole.example.dropwizard_atmosphere.dao.UserDAO;

/**
 * This class performs the Basic authentication for the REST endpoints. It's automatically mapped to @Auth
 * annotation by Dropwizard.
 * 
 * @author rogersole
 *
 */
public class ExampleAuthenticator implements Authenticator<BasicCredentials, User> {
    private final UserDAO dao;

    public ExampleAuthenticator(UserDAO dao) {
        super();
        this.dao = dao;
    }

    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        Optional<User> user = dao.authenticate(credentials);
        return user;
    }
}
