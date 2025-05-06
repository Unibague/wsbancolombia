package co.edu.unibague.ws.configurations;

import co.edu.unibague.ws.controllers.RINSoapController;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Custom class to add other configurations to Spring, besides the ones required to
 * start de service endpoint.
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
@Configuration
public class ControllersConfiguration {

    /**
     * Connection to the main Oracle database.
     *
     * @return Object of type {@link DataSource}
     */
    @Bean(name = "iceberg")
    @Primary
    @ConfigurationProperties(prefix = "iceberg.db")
    public DataSource icebergDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * Custom Jdbc object to make queries to the Oracle DB
     * @param iceberg Oracle {@link DataSource} object
     * @return Object of type {@link JdbcTemplate}
     */
    @Bean(name = "jdbcIceberg")
    public JdbcTemplate jdbcIceberg(DataSource iceberg){
        return new JdbcTemplate(iceberg);
    }

    /**
     * Connection to the "Ingles" Informix database.
     *
     * @return Object of type {@link DataSource}
     */
    @Bean(name = "sia")
    @ConfigurationProperties(prefix = "ingles.db")
    public DataSource siaDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * Custom Jdbc object to make queries to the Informix DB
     * @param sia Informix {@link DataSource} object
     * @return Object of type {@link JdbcTemplate}
     */
    @Bean(name = "jdbcIngles")
    public JdbcTemplate jdbcIngles(@Qualifier("sia") DataSource sia){
        return new JdbcTemplate(sia);
    }

    /**
     * Connection to the Postgres local database.
     *
     * @return Object of type {@link DataSource}
     */
    @Bean(name = "pg")
    @ConfigurationProperties(prefix = "postgres.db")
    public DataSource postgresDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * Custom Jdbc object to make queries to the Postgres DB
     * @param pg Postgres {@link DataSource} object
     * @return Object of type {@link JdbcTemplate}
     */
    @Bean(name = "jdbcPostgres")
    public JdbcTemplate jdbcPostgres(@Qualifier("pg") DataSource pg){
        return new JdbcTemplate(pg);
    }


    /**
     * Initializes a new instance of the RINSoapController, and makes it available for
     * auto-wiring in Spring.
     * @return new RINSoapController
     */
    @Bean
    public RINSoapController rinSoapController(){
        return new RINSoapController();
    }
}
