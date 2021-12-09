package dh0023.example.springbatchreal.config.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import java.util.Collections;

@Configuration
@EnableJpaRepositories(basePackages = "dh0023.example.springbatchreal.jobs")
@EnableTransactionManagement(proxyTargetClass = true)
public class MysqlJpaConfig {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Primary
    @Bean("mysqlJpaDataSource")
    @ConfigurationProperties(prefix = "databases.mysql.first")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean("mysqlJpaEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            EntityManagerFactoryBuilder builder, @Qualifier("mysqlJpaDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("dh0023.example.springbatchreal.jobs.**.jpa")
                .properties(Collections.singletonMap("hibernate.hdm2ddl.auto", ddlAuto))
                .build();
    }

    @Bean("mysqlJpaTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("mysqlJpaEntityManagerFactoryBean") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
