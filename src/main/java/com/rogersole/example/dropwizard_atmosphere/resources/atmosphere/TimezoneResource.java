package com.rogersole.example.dropwizard_atmosphere.resources.atmosphere;

import java.io.IOException;

import javax.ws.rs.Path;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.dto.ResponseErrorDTO;
import com.rogersole.example.dropwizard_atmosphere.dto.TimezoneDTO;
import com.rogersole.example.dropwizard_atmosphere.exception.TimezoneException;
import com.rogersole.example.dropwizard_atmosphere.logic.NRTimezone;

/**
 * Websocket endpoint to get the timezone convertion.
 * 
 * @author rogersole
 *
 */

@Path("/")
@AtmosphereHandlerService(path = "/websocket/timezone", broadcasterCache = UUIDBroadcasterCache.class, interceptors = {
                AtmosphereResourceLifecycleInterceptor.class, BroadcastOnPostAtmosphereInterceptor.class,
                TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class})
public class TimezoneResource extends WebSocketResource {
    private static final Logger log = LoggerFactory.getLogger(TimezoneResource.class);

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {

        if (!authenticated) {
            response.write(mapper.writeValueAsString("{\"error\": \"Authorization required\"}"));
            response.close();
            return;
        }

        log.debug("Calculting WEBSOCKET timezone for {}", message);

        Optional<String> timezone = Optional.of(message);
        try {
            String time = NRTimezone.calculateTimezone(timezone);
            response.write(mapper.writeValueAsString(new TimezoneDTO(timezone.get(), time)));
        }
        catch (TimezoneException ex) {
            response.write(mapper.writeValueAsString(new ResponseErrorDTO(ex.getClass().getName(), ex.getMessage())));
        }
    }
}
