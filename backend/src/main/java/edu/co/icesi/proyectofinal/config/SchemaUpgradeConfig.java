package edu.co.icesi.proyectofinal.config;

import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Pone al dia el esquema de la base de datos al arrancar, para que el proyecto se pueda
 * desplegar sin correr SQL a mano.
 */
@Configuration
public class SchemaUpgradeConfig {

    @Bean
    public MissingColumnsInitializer missingColumnsInitializer(DataSource dataSource) {
        return new MissingColumnsInitializer(dataSource);
    }

    /** Hibernate valida el esquema al crearse, asi que el parche tiene que correr antes. */
    @Bean
    public static EntityManagerFactoryDependsOnPostProcessor missingColumnsDependsOnPostProcessor() {
        return new EntityManagerFactoryDependsOnPostProcessor("missingColumnsInitializer");
    }
}
