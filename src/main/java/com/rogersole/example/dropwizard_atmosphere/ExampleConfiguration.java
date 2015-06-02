package com.rogersole.example.dropwizard_atmosphere;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogersole.example.dropwizard_atmosphere.configs.GitHubConfiguration;

/**
 * Contains the specific configuration parameters added to config/example.yml file which are not
 * already managed by Configuration class.
 * 
 * @author rogersole
 *
 */
public class ExampleConfiguration extends Configuration {
    @Valid
    @NotNull
    private GitHubConfiguration github   = new GitHubConfiguration();

    @Valid
    @NotNull
    private DataSourceFactory   database = new DataSourceFactory();

    @JsonProperty("github")
    public GitHubConfiguration getGithubConfiguration() {
        return github;
    }

    @JsonProperty("github")
    public void setGithubConfiguration(GitHubConfiguration github) {
        this.github = github;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }
}
