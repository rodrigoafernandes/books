package br.com.code.challenge;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class H2R2dbcConfig {

  @Inject
  @ConfigProperty(name = "r2dbc.database")
  String database;

  @Inject
  @ConfigProperty(name = "r2dbc.username")
  String username;

  @Inject
  @ConfigProperty(name = "r2dbc.url")
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
  @IfBuildProfile("test")
  ConnectionPool connectionPool() {
    final var connectionOptions = ConnectionFactoryOptions.builder()
        .option(DRIVER, "h2")
        .option(PROTOCOL, "mem")
        .option(DATABASE, database)
        .option(Option.sensitiveValueOf("DB_CLOSE_DELAY"), "-1")
        .option(Option.sensitiveValueOf("DB_CLOSE_ON_EXIT"), "FALSE")
        .option(USER, username)
        .option(PASSWORD, password)
        .build();

    final var factory = ConnectionFactories.get(connectionOptions);

    final var pollConfiguration = ConnectionPoolConfiguration.builder()
        .connectionFactory(factory)
        .maxSize(maxSize)
        .initialSize(initialSize)
        .maxIdleTime(Duration.ofMinutes(maxIdleTime))
        .validationQuery(validationQuery)
        .build();

    return new ConnectionPool(pollConfiguration);
  }

}
