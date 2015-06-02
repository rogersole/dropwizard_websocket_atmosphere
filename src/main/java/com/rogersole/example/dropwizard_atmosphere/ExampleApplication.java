package com.rogersole.example.dropwizard_atmosphere;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.servlet.ServletRegistration;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;

import com.rogersole.example.dropwizard_atmosphere.auth.ExampleAuthenticator;
import com.rogersole.example.dropwizard_atmosphere.configs.GitHubConfiguration;
import com.rogersole.example.dropwizard_atmosphere.core.Tuple;
import com.rogersole.example.dropwizard_atmosphere.core.User;
import com.rogersole.example.dropwizard_atmosphere.dao.TupleDAO;
import com.rogersole.example.dropwizard_atmosphere.dao.UserDAO;
import com.rogersole.example.dropwizard_atmosphere.resources.GitHubTopUsersResource;
import com.rogersole.example.dropwizard_atmosphere.resources.TimezoneResource;
import com.rogersole.example.dropwizard_atmosphere.resources.TupleResource;

/**
 * It's the main entry point of all the webserver application. It initializes the needed stuff,
 * register endpoints and resources and runs the webserver.
 * 
 * @author rogersole
 *
 */
public class ExampleApplication extends Application<ExampleConfiguration> {
    public static void main(String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    // bundles creations
    private final HibernateBundle<ExampleConfiguration> hibernateBundle = new HibernateBundle<ExampleConfiguration>(
                                                                                        Tuple.class, User.class) {
                                                                            public DataSourceFactory getDataSourceFactory(
                                                                                            ExampleConfiguration configuration) {
                                                                                return configuration
                                                                                                .getDataSourceFactory();
                                                                            }
                                                                        };

    @Override
    public String getName() {
        return "Example WebServer test";
    }

    @Override
    public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        // bootstrap.addCommand(new DAOEnvironmentCommand(this));
    }

    /**
     * Initializes websocket-based interface.
     * 
     * @param configuration
     * @param environment
     */
    private void initializeAtmosphere(ExampleConfiguration configuration, Environment environment) {
        AtmosphereServlet servlet = new AtmosphereServlet();
        servlet.framework().addInitParameter("com.sun.jersey.config.property.packages",
                        "example.dropwizard_atmoshpere.resources.websocket");
        servlet.framework().addInitParameter(ApplicationConfig.WEBSOCKET_CONTENT_TYPE, "application/json");
        servlet.framework().addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true");

        com.rogersole.example.dropwizard_atmosphere.resources.atmosphere.WebSocketResource
                        .setSessionFactory(hibernateBundle.getSessionFactory());

        ServletRegistration.Dynamic tzResourceHolder =
                        environment.servlets().addServlet(
                                        "example.dropwizard_atmoshpere.resources.websocket.TimezoneResource",
                                        servlet);
        tzResourceHolder.addMapping("/websocket/timezone/*");

        GitHubConfiguration ghconfig = configuration.getGithubConfiguration();

        ServletRegistration.Dynamic ghResourceHolder =
                        environment.servlets().addServlet(
                                        "example.dropwizard_atmoshpere.resources.websocket.GitHubTopUsersResource",
                                        servlet);
        ghResourceHolder.setInitParameter("user", ghconfig.getUser());
        ghResourceHolder.setInitParameter("pswd", ghconfig.getPswd());
        ghResourceHolder.setInitParameter("max_results", ghconfig.getMaxResults().toString());
        ghResourceHolder.addMapping("/websocket/github/*");

        ServletRegistration.Dynamic tupleResourceHolder =
                        environment.servlets().addServlet(
                                        "example.dropwizard_atmoshpere.resources.websocket.TupleResource",
                                        servlet);
        tupleResourceHolder.addMapping("/websocket/tuple/*");
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception {

        final TupleDAO tupleDAO = new TupleDAO(hibernateBundle.getSessionFactory());
        final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
        final BasicAuthFactory<User> authFactory =
                        new BasicAuthFactory<User>(new ExampleAuthenticator(userDAO), "REALM", User.class);

        GitHubConfiguration ghconfig = configuration.getGithubConfiguration();

        environment.jersey().register(AuthFactory.binder(authFactory));

        environment.jersey().register(new TimezoneResource());
        environment.jersey().register(new TupleResource(tupleDAO));
        environment.jersey().register(
                        new GitHubTopUsersResource(ghconfig.getUser(), ghconfig.getPswd(), ghconfig.getMaxResults()));

        initializeAtmosphere(configuration, environment);
    }
}
