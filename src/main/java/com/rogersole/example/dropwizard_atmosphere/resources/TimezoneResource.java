package com.rogersole.example.dropwizard_atmosphere.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.core.User;
import com.rogersole.example.dropwizard_atmosphere.dto.ResponseErrorDTO;
import com.rogersole.example.dropwizard_atmosphere.dto.TimezoneDTO;
import com.rogersole.example.dropwizard_atmosphere.exception.TimezoneException;
import com.rogersole.example.dropwizard_atmosphere.logic.NRTimezone;

/**
 * REST endpoint to get the datetime given a timezone name.
 * 
 * @author rogersole
 *
 */

@Produces(MediaType.APPLICATION_JSON)
@Path("rest/timezone")
public class TimezoneResource {
    private static final Logger log = LoggerFactory.getLogger(TimezoneResource.class);

    public TimezoneResource() {}

    @GET
    @UnitOfWork
    public Object processTimezoneRequest(@Auth User user, @QueryParam("tz") Optional<String> timezone) {
        try {
            log.debug("Calculting REST timezone for {}", timezone.get());
            String time = NRTimezone.calculateTimezone(timezone);
            return new TimezoneDTO(timezone.get(), time);
        }
        catch (TimezoneException ex) {
            return new ResponseErrorDTO(ex.getClass().getName(), ex.getMessage());
        }
    }
}
