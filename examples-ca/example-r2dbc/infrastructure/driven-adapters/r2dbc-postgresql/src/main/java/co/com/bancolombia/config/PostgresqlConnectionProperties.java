package co.com.bancolombia.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.postgres")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String dbname,
        String schema,
        String username,
        String password) {
}
