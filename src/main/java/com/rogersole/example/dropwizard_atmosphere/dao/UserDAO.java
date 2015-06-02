package com.rogersole.example.dropwizard_atmosphere.dao;

import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.core.User;

/**
 * Data Access Object to manage User objects and interact with database.
 * 
 * @author rogersole
 *
 */
public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<User> authenticate(BasicCredentials credentials) {
        return authenticate(credentials.getUsername(), credentials.getPassword());
    }

    public Optional<User> authenticate(String username, String password) {
        Query query =
                        currentSession().createQuery("from User u WHERE u.name = :name AND u.password = :password")
                                        .setParameter("name", username).setParameter("password", password);
        User user = uniqueResult(query);
        return Optional.fromNullable(user);
    }

}
