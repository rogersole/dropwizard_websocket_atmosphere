package com.rogersole.example.dropwizard_atmosphere.resources.atmosphere;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.OnMessage;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.core.User;
import com.rogersole.example.dropwizard_atmosphere.dao.UserDAO;

/**
 * Websocket endpoint superclass that performs the authentication.
 * 
 * @author rogersole
 *
 */
public class WebSocketResource extends OnMessage<String> {

    private static final Logger     log    = LoggerFactory.getLogger(WebSocketResource.class);
    protected static SessionFactory sessionFactory;
    protected static UserDAO        userDAO;

    protected final ObjectMapper    mapper = new ObjectMapper();
    protected boolean               authenticated;

    public static void setSessionFactory(SessionFactory sessionFactory) {
        WebSocketResource.sessionFactory = sessionFactory;
        WebSocketResource.userDAO = new UserDAO(WebSocketResource.sessionFactory);
    }

    @Override
    public void onOpen(AtmosphereResource resource) throws IOException {

        final String authorization = resource.getRequest().getHeader("Authorization");
        log.debug("Authorization: {}", authorization);

        AtmosphereResponse response = resource.getResponse();

        if (authorization == null || !authorization.startsWith("Basic")) {
            response.write(mapper.writeValueAsString("{\"error\": \"Basic authorization expected\"}"));
            return;
        }

        authenticated = false;

        // Authorization: Basic base64credentials
        String base64Credentials = authorization.substring("Basic".length()).trim();
        String credentials = new String(Base64.decodeBase64(base64Credentials), Charset.forName("UTF-8"));
        // credentials = username:password
        final String[] values = credentials.split(":", 2);

        Session session = null;
        try {
            // Open a new session and make it managed by dropwizard-hibernate,
            // this way, existing DAO can be used without any extra implementation.
            session = sessionFactory.openSession();
            session.setDefaultReadOnly(true);
            session.setCacheMode(CacheMode.NORMAL);
            session.setFlushMode(FlushMode.MANUAL);
            ManagedSessionContext.bind(session);

            Optional<User> user = userDAO.authenticate(values[0], values[1]);
            if (user.isPresent())
                authenticated = true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            session.close();
        }
    }

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        // nothing to be done here. Overriden by all the subclasses.
    }
}
