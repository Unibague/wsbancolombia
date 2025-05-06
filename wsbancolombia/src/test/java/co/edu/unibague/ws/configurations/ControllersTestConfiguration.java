package co.edu.unibague.ws.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Primary;
import org.tempuri.RecaudosRINSoap;
import co.edu.unibague.ws.endpoint.RinService;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@Configuration
@Import(ControllersConfiguration.class)
public class ControllersTestConfiguration {

    @Value(value="classpath:datasource.properties")
    private Resource dbTest;

    @Bean
    public RecaudosRINSoap recaudosRINSoap(){
        return new RinService();
    }

    @Bean(name = "iceberg")
    @Primary
    public DataSource dataSource() throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        try {
            Properties properties = new Properties();
            properties.load(dbTest.getInputStream());
            dataSource.setUser(properties.getProperty("iceberg.username"));
            dataSource.setPassword(properties.getProperty("iceberg.password"));
            dataSource.setURL(properties.getProperty("iceberg.url"));
            dataSource.setImplicitCachingEnabled(true);
// dataSource.setFastConnectionFailoverEnabled(true);
}catch (IOException e){

        }
        return dataSource;
    }

    @Bean(name = "sia")
    public DataSource sia(){
        try {
            Properties properties = new Properties();
            properties.load(dbTest.getInputStream());

            return DataSourceBuilder.create().username(properties.getProperty("informix.username"))
                    .password(properties.getProperty("informix.password"))
                    .url(properties.getProperty("informix.url"))
                    .driverClassName(properties.getProperty("informix.driver-class-name")).build();
        }catch (IOException e){

        }
        return null;
    }

    @Bean(name = "pg")
    public DataSource postgres(){
        try {
            Properties properties = new Properties();
            properties.load(dbTest.getInputStream());

            return DataSourceBuilder.create().username(properties.getProperty("postgres.username"))
                    .url(properties.getProperty("postgres.url"))
                    .driverClassName(properties.getProperty("postgres.driver-class-name")).build();
        }catch (IOException e){

        }
        return null;
    }

    @Bean(name = "jdbcIceberg")
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "jdbcIngles")
    public JdbcTemplate jdbcIngles(@Qualifier("sia") DataSource sia){
        return new JdbcTemplate(sia);
    }

    @Bean(name = "jdbcPostgres")
    public JdbcTemplate jdbcPostgres(@Qualifier("pg") DataSource pg){
        return new JdbcTemplate(pg);
    }
}
