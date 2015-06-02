package com.rogersole.example.dropwizard_atmosphere.dto;

import io.dropwizard.jackson.JsonSnakeCase;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Data Transfer Object that maps a GitHubUser. It's the object returned (converted to JSON) on the
 * responses realted with github endpoints.
 * 
 * @author rogersole
 *
 */

@JsonSnakeCase
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"email", "name", "repositories"})
public class GitHubUserDTO {
    @JsonProperty
    private String       email        = null;

    @JsonProperty
    private String       name         = null;

    @JsonProperty
    private List<String> repositories = null;

    // fields used to parse HTTP response from GitHub v2 API
    @JsonIgnore
    private String       url          = null;
    @JsonIgnore
    private String       reposUrl     = null;

    public GitHubUserDTO(String url) {
        this.url = url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public void addRepository(String repoName) {
        if (repositories == null)
            repositories = new ArrayList<String>();
        repositories.add(repoName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=").append(name).append("\n");
        sb.append("email=").append(email).append("\n");
        sb.append("url=").append(url).append("\n");
        sb.append("reposUrl=").append(reposUrl).append("\n");
        if (repositories != null)
            sb.append("repositories=").append(repositories.toString()).append("\n");
        return sb.toString();
    }
}
