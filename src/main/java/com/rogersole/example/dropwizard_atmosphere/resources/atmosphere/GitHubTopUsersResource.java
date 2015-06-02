package com.rogersole.example.dropwizard_atmosphere.resources.atmosphere;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Path;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.dto.GitHubUserDTO;
import com.rogersole.example.dropwizard_atmosphere.dto.ResponseErrorDTO;
import com.rogersole.example.dropwizard_atmosphere.exception.GitHubTopUsersException;
import com.rogersole.example.dropwizard_atmosphere.logic.GitHubTopUsers;

/**
 * Websocket endpoint to get the githup top active users.
 * 
 * @author rogersole
 *
 */

@Path("/")
@AtmosphereHandlerService(path = "/websocket/github/topactive", broadcasterCache = UUIDBroadcasterCache.class,
                interceptors = {AtmosphereResourceLifecycleInterceptor.class,
                                BroadcastOnPostAtmosphereInterceptor.class,
                                TrackMessageSizeInterceptor.class, HeartbeatInterceptor.class})
public class GitHubTopUsersResource extends WebSocketResource {

    private static final Logger log        = LoggerFactory.getLogger(GitHubTopUsersResource.class);

    private String              ghUser     = null;
    private String              ghPswd     = null;
    private Integer             maxResults = null;

    @Override
    public void onOpen(AtmosphereResource resource) throws IOException {
        super.onOpen(resource);
        if (authenticated) {
            this.ghUser = resource.getAtmosphereConfig().getInitParameter("user");
            this.ghPswd = resource.getAtmosphereConfig().getInitParameter("pswd");
            this.maxResults = Integer.parseInt(resource.getAtmosphereConfig().getInitParameter("max_results"));
        }
    }

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {

        if (!authenticated) {
            response.write(mapper.writeValueAsString("{\"error\": \"Authorization required\"}"));
            response.close();
            return;
        }

        log.debug("Calculating top active GitHub users WEBSOCKET for city: {}", message);

        Optional<String> city = Optional.of(message);
        try {
            if (ghUser == null || ghPswd == null || maxResults == null)
                throw new GitHubTopUsersException("User and/or Password not specified");
            GitHubTopUsers gh = new GitHubTopUsers(ghUser, ghPswd, city, maxResults);
            List<GitHubUserDTO> result = gh.calculateTopInCity();
            response.write(mapper.writeValueAsString(result));
        }
        catch (GitHubTopUsersException ex) {
            response.write(mapper.writeValueAsString(new ResponseErrorDTO(ex.getClass().getName(), ex.getMessage())));
        }
    }
}
