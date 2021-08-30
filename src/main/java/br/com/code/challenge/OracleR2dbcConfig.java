package br.com.code.challenge;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.time.Duration;

public class OracleR2dbcConfig {

  @Inject
  @ConfigProperty(name = "r2dbc.url")
  String url;

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
    final var factory = ConnectionFactories.get(url);

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
