package com.rogersole.example.dropwizard_atmosphere.exception;

import javax.ws.rs.WebApplicationException;

import org.eclipse.jetty.http.HttpStatus;

/**
 * Exception used when something happens on the GitHub endpoints. Inheriting from
 * WebApplicationException means that is automatically converted into a Response object.
 * 
 * @author rogersole
 *
 */
public class GitHubTopUsersException extends WebApplicationException {
    private static final long serialVersionUID = 4830770638572088873L;

    public GitHubTopUsersException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST_400);
    }
}
