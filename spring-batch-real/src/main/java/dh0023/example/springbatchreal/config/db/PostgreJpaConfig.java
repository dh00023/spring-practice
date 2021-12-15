package dh0023.example.springbatchreal.config.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

import static dh0023.example.springbatchreal.config.db.PostgreJpaConfig.*;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
@EnableJpaRepositories(basePackages = PACKAGE
        , entityManagerFactoryRef = P_ENTITY_MANAGER_FACTORY
        , transactionManagerRef = P_TX_MANAGER)
public class PostgreJpaConfig {
    private static final String PROPERTIES = "databases.postgre.properties";

    public static final String PACKAGE = "dh0023.example.springbatchreal.jobs.postgre";
    public static final String P_ENTITY_MANAGER_FACTORY = "postgreEntityManagerFactory";
    public static final String P_READER_ENTITY_MANAGER_FACTORY = "postgreReaderEntityManagerFactory";

    public static final String P_TX_MANAGER = "postgreTransactionManager";


    @Bean
    @ConfigurationProperties(PROPERTIES)
    public HashMap<String, String> properties() {
        return new HashMap<>();
    }

    @Bean(P_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            EntityManagerFactoryBuilder builder, @Qualifier(PostgreBaseConfig.POSTGRE_DATASOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(PACKAGE)
                .properties(properties())
                .build();
    }

    @Bean(P_READER_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean readerEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder, @Qualifier(PostgreBaseConfig.POSTGRE_READER_DATASOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(PACKAGE)
                .properties(properties())
                .build();
    }


    @Bean(P_TX_MANAGER)
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
