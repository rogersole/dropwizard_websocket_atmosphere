package com.rogersole.example.dropwizard_atmosphere.exception;

import javax.ws.rs.WebApplicationException;

import org.eclipse.jetty.http.HttpStatus;

/**
 * Exception used when something happens on the timezone endpoints. Inheriting from
 * WebApplicationException means that is automatically converted into a Response object.
 * 
 * @author rogersole
 *
 */

public class TimezoneException extends WebApplicationException {
    private static final long serialVersionUID = -1103111624562407444L;

    public TimezoneException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST_400);
    }
}
