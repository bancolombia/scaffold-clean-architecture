package co.com.bancolombia.r2postgresql.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class PostgreSQLConnectionPool {

    private final PostgresqlConnectionProperties connectionProperties;
    private final PostgreSQLConnectionPoolProperties connectionPoolProperties;

    @Bean
    public ConnectionPool buildConnectionConfiguration() {
        PostgresqlConnectionConfiguration dbConfiguration =
                PostgresqlConnectionConfiguration.builder()
                        .host(connectionProperties.host())
                        .port(connectionProperties.port())
                        .database(connectionProperties.database())
                        .schema(connectionProperties.schema())
                        .username(connectionProperties.username())
                        .password(connectionProperties.password())
                        .build();

        ConnectionPoolConfiguration poolConfiguration =
                ConnectionPoolConfiguration.builder()
                        .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
                        .name("api-postgres-connection-pool")
                        .initialSize(connectionPoolProperties.initialSize())
                        .maxSize(connectionPoolProperties.maxSize())
                        .maxIdleTime(Duration.ofMinutes(connectionPoolProperties.maxIdleTime()))
                        .build();

        return new ConnectionPool(poolConfiguration);
    }
}
