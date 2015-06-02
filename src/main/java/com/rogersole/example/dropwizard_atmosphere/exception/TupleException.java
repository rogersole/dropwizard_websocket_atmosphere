package com.rogersole.example.dropwizard_atmosphere.exception;

import javax.ws.rs.WebApplicationException;

import org.eclipse.jetty.http.HttpStatus;

/**
 * Exception used when something happens on the Database interaction endpoints. Inheriting from
 * WebApplicationException means that is automatically converted into a Response object.
 * 
 * @author rogersole
 *
 */
public class TupleException extends WebApplicationException {

    private static final long serialVersionUID = -6360788433139811764L;

    public TupleException(String msg) {
        super(msg, HttpStatus.INTERNAL_SERVER_ERROR_500);
    }
}
