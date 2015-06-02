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
import com.rogersole.example.dropwizard_atmosphere.exception.GitHubTopUsersException;
import com.rogersole.example.dropwizard_atmosphere.logic.GitHubTopUsers;

/**
 * REST endpoint to get the githup top active users.
 * 
 * @author rogersole
 *
 */

@Produces(MediaType.APPLICATION_JSON)
@Path("rest/github/topactive")
public class GitHubTopUsersResource {
    private static final Logger log = LoggerFactory.getLogger(GitHubTopUsersResource.class);

    private String              ghUser;
    private String              ghPswd;
    private int                 maxResults;

    public GitHubTopUsersResource(String ghUser, String ghPswd, int maxResults) {
        this.ghUser = ghUser;
        this.ghPswd = ghPswd;
        this.maxResults = maxResults;
    }

    @GET
    @UnitOfWork
    public Object processTopActivePerCityRequest(@Auth User user, @QueryParam("city") Optional<String> city) {

        log.debug("Calculating top active GitHub users REST for city: {}", city.get());

        try {
            GitHubTopUsers gh = new GitHubTopUsers(ghUser, ghPswd, city, maxResults);
            return gh.calculateTopInCity();
        }
        catch (GitHubTopUsersException ex) {
            return new ResponseErrorDTO(ex.getClass().getName(), ex.getMessage());
        }
    }
}
