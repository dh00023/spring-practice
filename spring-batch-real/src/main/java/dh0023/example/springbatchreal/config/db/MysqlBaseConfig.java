package dh0023.example.springbatchreal.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MysqlBaseConfig {

    private static final String PROPERTIES = "databases.mysql.datasource.hikari";
    public static final String MAIN_DATASOURCE = "mainDatasource";
    public static final String MAIN_READER_DATASOURCE = "mainReaderDatasource";
    /**
     * 스프링 배치 메타 DB 정보가 있는 DB
     * @return
     */
    @Bean(MAIN_DATASOURCE)
    @Primary
    @ConfigurationProperties(prefix = PROPERTIES)
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(MAIN_READER_DATASOURCE)
    @ConfigurationProperties(prefix = PROPERTIES)
    public DataSource readerDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
        dataSource.setReadOnly(true);
        return dataSource;
    }
}
