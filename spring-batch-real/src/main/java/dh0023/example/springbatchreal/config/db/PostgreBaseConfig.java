package dh0023.example.springbatchreal.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PostgreBaseConfig {

    private static final String PROPERTIES = "databases.postgre.datasource.hikari";
    public static final String POSTGRE_DATASOURCE = "postgreDatasource";
    public static final String POSTGRE_READER_DATASOURCE = "postgreReaderDatasource";
    /**
     * 스프링 배치 메타 DB 정보가 있는 DB
     * @return
     */
    @Bean(POSTGRE_DATASOURCE)
    @ConfigurationProperties(prefix = PROPERTIES)
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(POSTGRE_READER_DATASOURCE)
    @ConfigurationProperties(prefix = PROPERTIES)
    public DataSource readerDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
        dataSource.setReadOnly(true);
        return dataSource;
    }
}
