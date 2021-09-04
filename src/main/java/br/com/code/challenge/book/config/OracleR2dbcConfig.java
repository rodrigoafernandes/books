package br.com.code.challenge.book.config;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class OracleR2dbcConfig {

  @Inject
  @ConfigProperty(name = "r2dbc.url")
  String url;

  @Inject
  @ConfigProperty(name = "r2dbc.host")
  String host;

  @Inject
  @ConfigProperty(name = "r2dbc.port")
  String port;

  @Inject
  @ConfigProperty(name = "r2dbc.database")
  String database;

  @Inject
  @ConfigProperty(name = "r2dbc.username")
  String username;

  @Inject
  @ConfigProperty(name = "r2dbc.password")
  String password;

  @Inject
  @ConfigProperty(name = "r2dbc.max-size")
  int maxSize;

  @Inject
  @ConfigProperty(name = "r2dbc.initial-size")
  int initialSize;

  @Inject
  @ConfigProperty(name = "r2dbc.max-idle-time")
  long maxIdleTime;

  @Inject
  @ConfigProperty(name = "r2dbc.validation-query")
  String validationQuery;

  @Produces
  @UnlessBuildProfile("test")
  ConnectionPool connectionPool() {
    final var factoryOptions = ConnectionFactoryOptions.builder()
        .option(DRIVER, "oracle")
        .option(HOST, host)
        .option(PORT, Integer.valueOf(port))
        .option(DATABASE, database)
        .option(USER, username)
        .option(PASSWORD, password)
        .build();

    final var factory = ConnectionFactories.get(factoryOptions);

    final var pollConfiguration = ConnectionPoolConfiguration.builder()
        .connectionFactory(factory)
        .maxSize(maxSize)
        .initialSize(initialSize)
        .maxIdleTime(Duration.ofMinutes(maxIdleTime))
        .validationQuery(validationQuery)
        .registerJmx(false)
        .build();

    return new ConnectionPool(pollConfiguration);
  }

}
