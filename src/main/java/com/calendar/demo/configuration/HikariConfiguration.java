package com.calendar.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class HikariConfiguration {
    @Value ("${datasource.driverClassName}")
    private String driverClass;

    @Value ("${datasource.url}")
    private String dbURL;

    @Value ("${datasource.username}")
    private String userName;

    @Value ("${datasource.password}")
    private String password;

    @Value ("${spring.datasource.hikari.jdbcMaxPoolSize}")
    private int jdbcMaxPoolSize;

    @Value ("${spring.datasource.hikari.jdbiMaxPoolSize}")
    private int jdbiMaxPoolSize;

    @Value ("${spring.datasource.hikari.maxIdleTime}")
    private int maxIdleTime;

    @Value ("${spring.datasource.hikari.maxLifeTime}")
    private int maxLifeTime;

    @Value ("${spring.datasource.hikari.testStatement}")
    private String testStatement;

    @Bean
    @Primary
    public HikariDataSource getJdbcDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("Hikari-JDBC-Pool");
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(dbURL);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setConnectionTimeout(1000); //Setting timeout to 1 second (default is 30 seconds)
        dataSource.setMaximumPoolSize(jdbcMaxPoolSize);
        dataSource.setIdleTimeout(maxIdleTime);
        dataSource.setMaxLifetime(maxLifeTime);
        dataSource.setConnectionInitSql(testStatement);
        return dataSource;
    }
}
