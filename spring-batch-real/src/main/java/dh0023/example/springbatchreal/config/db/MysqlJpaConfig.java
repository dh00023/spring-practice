package dh0023.example.springbatchreal.config.db;

import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.source.spi.PluralAttributeKeySource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

import static dh0023.example.springbatchreal.config.db.MysqlBaseConfig.MAIN_READER_DATASOURCE;
import static dh0023.example.springbatchreal.config.db.MysqlJpaConfig.PACKAGE;


@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
@EnableJpaRepositories(basePackages = PACKAGE)
public class MysqlJpaConfig {
    private static final String PROPERTIES = "databases.mysql.properties";

    public static final String PACKAGE = "dh0023.example.springbatchreal";
    public static final String MAIN_ENTITY_MANAGER_FACTORY = "mainEntityManagerFactory";
    public static final String MAIN_READER_ENTITY_MANAGER_FACTORY = "mainReaderEntityManagerFactory";

    public static final String MAIN_TX_MANAGER = "mainTransactionManager";


    @Bean
    @ConfigurationProperties(PROPERTIES)
    public HashMap<String, Object> properties() {
        return new HashMap<>();
    }

    @Primary
    @Bean(MAIN_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            EntityManagerFactoryBuilder builder, DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(PACKAGE)
                .properties(properties())
                .build();
    }

    @Bean(MAIN_READER_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean readerEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder, @Qualifier(MAIN_READER_DATASOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(PACKAGE)
                .properties(properties())
                .build();
    }
//
//    @ConfigurationProperties(prefix = "databases.mysql.hibernate.datasource.hikari")
//    public HikariConfig hikariConfig() {
//        return new HikariConfig();
//    }

    @Primary
    @Bean(MAIN_TX_MANAGER)
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
