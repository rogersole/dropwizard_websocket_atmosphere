package com.rogersole.example.dropwizard_atmosphere.configs;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class maps the configuration .yml file github variables.
 * 
 * @author rogersole
 *
 */
public class GitHubConfiguration {
    @NotNull
    @JsonProperty
    private String  user;

    @NotNull
    @JsonProperty
    private String  pswd;

    @NotNull
    @JsonProperty
    private Integer maxResults;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}
