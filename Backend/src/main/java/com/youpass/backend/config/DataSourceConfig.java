package com.youpass.backend.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        String dbUrl = System.getenv("DB_Url");
        dataSource.setJdbcUrl( dbUrl != null ? dbUrl : "jdbc:postgresql://localhost:5432/CloneYoupass");
        // chống bị crash app khi bị biến môi trường null, tạo một fallback cho biến môi trường

        String dbUsername = System.getenv("DB_Username");
        dataSource.setUsername( dbUsername != null ? dbUsername : "postgres");

        String dbPassword = System.getenv("DB_Password");
        dataSource.setPassword(dbPassword != null ? dbPassword : "03062006");

        dataSource.setMaximumPoolSize(15);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        return dataSource;
    }
}
