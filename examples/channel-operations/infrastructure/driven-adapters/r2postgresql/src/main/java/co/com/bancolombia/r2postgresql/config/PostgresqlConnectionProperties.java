package co.com.bancolombia.r2postgresql.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostgresqlConnectionProperties {

  private String database;
  private String schema;
  private String username;
  private String password;
  private String host;
  private Integer port;
}
