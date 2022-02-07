package co.com.bancolombia.r2postgresql.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostgreSQLConnectionPool {

  public static final int INITIAL_SIZE = 12;
  public static final int MAX_SIZE = 15;
  public static final int MAX_IDLE_TIME = 30;

  @Bean
  public ConnectionPool getConnectionConfig() {
    // TODO: Config this props from secrets manager
    PostgresqlConnectionProperties pgProperties = new PostgresqlConnectionProperties();
    pgProperties.setDatabase("channeldb");
    pgProperties.setHost("localhost");
    pgProperties.setPort(5432);
    pgProperties.setUsername("postgres");
    pgProperties.setPassword("secret");
    pgProperties.setSchema("management");

    return buildConnectionConfiguration(pgProperties);
  }

  private ConnectionPool buildConnectionConfiguration(PostgresqlConnectionProperties properties) {
    PostgresqlConnectionConfiguration dbConfiguration =
        PostgresqlConnectionConfiguration.builder()
            .host(properties.getHost())
            .port(properties.getPort())
            .database(properties.getDatabase())
            .schema(properties.getSchema())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .build();

    ConnectionPoolConfiguration poolConfiguration =
        ConnectionPoolConfiguration.builder()
            .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
            .name("api-postgres-connection-pool")
            .initialSize(INITIAL_SIZE)
            .maxSize(MAX_SIZE)
            .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
            .validationQuery("SELECT 1")
            .build();

    return new ConnectionPool(poolConfiguration);
  }
}
