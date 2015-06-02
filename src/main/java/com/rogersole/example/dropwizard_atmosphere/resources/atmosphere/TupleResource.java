package com.rogersole.example.dropwizard_atmosphere.resources.atmosphere;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.ws.rs.Path;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogersole.example.dropwizard_atmosphere.core.Tuple;
import com.rogersole.example.dropwizard_atmosphere.dao.TupleDAO;
import com.rogersole.example.dropwizard_atmosphere.dto.ResponseErrorDTO;

/**
 * Websocket endpoint to store tuple information into database.
 * 
 * @author rogersole
 *
 */

@Path("/")
@AtmosphereHandlerService(path = "/websocket/tuple", broadcasterCache = UUIDBroadcasterCache.class, interceptors = {
                AtmosphereResourceLifecycleInterceptor.class, BroadcastOnPostAtmosphereInterceptor.class,
                TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class})
public class TupleResource extends WebSocketResource {

    private static final Logger log = LoggerFactory.getLogger(TupleResource.class);
    //
    private TupleDAO            tupleDAO;


    @Override
    public void init(AtmosphereConfig config) throws ServletException {
        tupleDAO = new TupleDAO(sessionFactory);
    }

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {

        if (!authenticated) {
            response.write(mapper.writeValueAsString("{\"error\": \"Authorization required\"}"));
            response.close();
            return;
        }

        Session session = null;
        try {
            // Open a new session and make it managed by dropwizard-hibernate,
            // this way, existing DAO can be used without any extra implementation.
            session = sessionFactory.openSession();
            session.setDefaultReadOnly(true);
            session.setCacheMode(CacheMode.NORMAL);
            session.setFlushMode(FlushMode.MANUAL);
            ManagedSessionContext.bind(session);

            Tuple tuple = new Tuple(mapper.readTree(message));
            log.debug("Received WEBSOCKET tuple {}", tuple);
            tupleDAO.create(tuple);

            response.write(mapper.writeValueAsString(tuple));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            response.write(mapper.writeValueAsString(new ResponseErrorDTO(ex.getClass().getName(), ex.getMessage())));
        }
        finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }
}
