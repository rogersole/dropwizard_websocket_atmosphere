package com.rogersole.example.dropwizard_atmosphere.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.rogersole.example.dropwizard_atmosphere.dto.GitHubUserDTO;
import com.rogersole.example.dropwizard_atmosphere.exception.GitHubTopUsersException;

/**
 * Performs all the logic related to GitHub API access to retrieve the top active users given a
 * city.
 * 
 * @author rogersole
 *
 */
public class GitHubTopUsers {

    private static final Logger log               = LoggerFactory.getLogger(GitHubTopUsers.class);
    private static String       SEARCH_USERS_BASE = "https://api.github.com/search/users";
    //
    private Optional<String>    city;
    private int                 maxResults;
    private CredentialsProvider credsProvider;

    public GitHubTopUsers(String user, String pswd, Optional<String> city, int maxResults) {
        this.city = city;
        this.maxResults = maxResults;
        credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope("localhost", 443), new UsernamePasswordCredentials(user, pswd));
    }

    /**
     * Using the class properties defined, performs the search over users with more activity in the
     * city.
     * 
     * @return List<GitHubUserDTO>
     * @throws GitHubTopUsersException
     */
    public List<GitHubUserDTO> calculateTopInCity() throws GitHubTopUsersException {

        // check if the city is supplied
        if (!city.isPresent() || (city.isPresent() && city.get().length() == 0))
            throw new GitHubTopUsersException("No city specified");

        try {
            // from initial api endpoint, obtain the top 5 contributors urls
            List<GitHubUserDTO> users = getLocationTopUserURLS();

            // create one thread per each user info to be retrieved
            ExecutorService taskExecutor = Executors.newFixedThreadPool(Math.min(maxResults, users.size()));

            // Execute in a separated thread each API request for the user details and repos
            for (GitHubUserDTO user : users) {
                GitHubWorker worker = new GitHubWorker(user, credsProvider);
                taskExecutor.execute(worker);
            }
            taskExecutor.shutdown();
            // wait until tasks completed to ensure the user objects contains the retrieved content
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            return users;
        }
        catch (Exception ex) {
            throw new GitHubTopUsersException(ex.getMessage());
        }
    }

    private List<GitHubUserDTO> getLocationTopUserURLS() throws Exception {

        // create results array of users
        List<GitHubUserDTO> users = new ArrayList<GitHubUserDTO>();

        // build http URI with the query parameters
        StringBuilder sb = new StringBuilder();
        sb.append(SEARCH_USERS_BASE).append("?q=location:").append(city.get()).append("+sort:repositories");
        HttpGet httpget = new HttpGet(sb.toString());

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            // create http client using provided credentials
            httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
            log.debug("Executing request: {}", httpget.getRequestLine());
            // execute the request
            response = httpclient.execute(httpget);

            // check response status code
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != HttpStatus.OK_200)
                throw new GitHubTopUsersException("GitHub API query returned status: " + responseCode
                                + " for request line: "
                                + httpget.getRequestLine());

            // parse the json data returned
            byte[] jsonData = EntityUtils.toByteArray(response.getEntity());
            JsonNode rootNode = new ObjectMapper().readTree(jsonData);
            JsonNode itemsNode = rootNode.path("items");
            Iterator<JsonNode> elements = itemsNode.elements();
            // Get the five top users' urls
            for (int i = 0; i < maxResults && elements.hasNext(); ++i) {
                JsonNode item = elements.next();
                GitHubUserDTO user = new GitHubUserDTO(item.path("url").asText());
                users.add(user);
            }

            return users;
        }
        catch (Exception ex) {
            throw new GitHubTopUsersException(ex.getMessage());
        }
        finally {
            try {
                if (response != null)
                    response.close();
                if (httpclient != null)
                    httpclient.close();
            }
            catch (Exception dummy) {}
        }
    }
}


/**
 * Class to encapsulate all the work to be done per user. Each worker is executed in a single
 * thread.
 * 
 * @author rogersole
 *
 */
class GitHubWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GitHubWorker.class);
    //
    private GitHubUserDTO       user;
    private CredentialsProvider credsProvider;
    private ObjectMapper        objectMapper;

    public GitHubWorker(GitHubUserDTO user, CredentialsProvider credsProvider) {
        this.user = user;
        this.credsProvider = credsProvider;
    }

    public void run() {

        // create credentials for the GitHub request
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

        objectMapper = new ObjectMapper();

        try {
            // retrieve user information
            getUserInformation(httpclient, user);
            // retrieve user repositories
            getUserRepos(httpclient, user);

        }
        catch (Exception ex) {
            throw new GitHubTopUsersException(ex.getMessage());
        }
        finally {
            try {
                httpclient.close();
            }
            catch (IOException dummy) {}
        }
    }

    private void getUserInformation(CloseableHttpClient httpclient, GitHubUserDTO user) throws Exception {
        HttpGet httpget = new HttpGet(user.getUrl());
        log.debug("Executing request: {}", httpget.getRequestLine());
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != HttpStatus.OK_200)
                throw new GitHubTopUsersException("GitHub API query returned status: " + responseCode
                                + " for request line: "
                                + httpget.getRequestLine());

            byte[] jsonData = EntityUtils.toByteArray(response.getEntity());
            JsonNode rootNode = objectMapper.readTree(jsonData);
            user.setName(rootNode.get("name").asText());
            user.setEmail(rootNode.get("email").asText());
            user.setReposUrl(rootNode.get("repos_url").asText());
        }
        finally {
            response.close();
        }
    }

    private void getUserRepos(CloseableHttpClient httpclient, GitHubUserDTO user) throws Exception {
        HttpGet httpget = new HttpGet(user.getReposUrl());
        log.debug("Executing request: {}", httpget.getRequestLine());
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != HttpStatus.OK_200)
                throw new GitHubTopUsersException("GitHub API query returned status: " + responseCode
                                + " for request line: "
                                + httpget.getRequestLine());

            byte[] jsonData = EntityUtils.toByteArray(response.getEntity());
            JsonNode rootNode = objectMapper.readTree(jsonData);
            Iterator<JsonNode> elems = rootNode.elements();
            while (elems.hasNext()) {
                user.addRepository(elems.next().get("name").asText());
            }
        }
        finally {
            response.close();
        }
    }
}
