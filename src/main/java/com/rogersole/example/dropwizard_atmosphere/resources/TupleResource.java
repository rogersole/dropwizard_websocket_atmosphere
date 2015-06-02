package com.rogersole.example.dropwizard_atmosphere.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogersole.example.dropwizard_atmosphere.core.Tuple;
import com.rogersole.example.dropwizard_atmosphere.core.User;
import com.rogersole.example.dropwizard_atmosphere.dao.TupleDAO;
import com.rogersole.example.dropwizard_atmosphere.dto.ResponseErrorDTO;

/**
 * REST endpoint to store tuples into database.
 * 
 * @author rogersole
 *
 */

@Produces(MediaType.APPLICATION_JSON)
@Path("rest/tuple")
public class TupleResource {
    private static final Logger log = LoggerFactory.getLogger(TupleResource.class);

    private final TupleDAO      tupleDAO;

    public TupleResource(TupleDAO tupleDAO) {
        this.tupleDAO = tupleDAO;
    }

    @GET
    @UnitOfWork
    public List<Tuple> getAll(@Auth User user) {
        return tupleDAO.findAll();
    }

    @POST
    @UnitOfWork
    public Object postTupleRequest(@Auth User user, Tuple tuple) {
        try {
            log.debug("Received REST tuple {}", tuple);
            tupleDAO.create(tuple);
            return Response.ok().build();
        }
        catch (Exception ex) {
            return new ResponseErrorDTO(ex.getClass().getName(), ex.getMessage());
        }
    }
}
