package co.com.crudtest.jpa.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class JpaConfig {

  @Bean
  public DBSecret dbSecret(Environment env) {
    return DBSecret.builder()
        .url(env.getProperty("spring.datasource.url"))
        .username(env.getProperty("spring.datasource.username"))
        .password(env.getProperty("spring.datasource.password"))
        .build();
  }

  @Bean
  public DataSource datasource(DBSecret secret, @Value("${spring.datasource.driverClassName}") String driverClass) {
    var config = new HikariConfig();
    config.setJdbcUrl(secret.getUrl());
    config.setUsername(secret.getUsername());
    config.setPassword(secret.getPassword());
    config.setDriverClassName(driverClass);
    return new HikariDataSource(config);
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource, @Value("${spring.jpa.databasePlatform}") String dialect) {
    var em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("co.com.crudtest.jpa");

    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", dialect);
    properties.setProperty("hibernate.hbm2ddl.auto", "update");
    em.setJpaProperties(properties);

    return em;
  }
}
