package dh0023.example.springbatchreal.jobs.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@EnableBatchProcessing
@SpringBatchTest
@ContextConfiguration(classes={
        MysqlMybatisPagingConfiguration.class,
        MysqlMybatisPagingUnitTest.TestDataSourceConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + MysqlMybatisPagingConfiguration.JOB_NAME})
class MysqlMybatisPagingUnitTest {

    @Autowired
    private MyBatisPagingItemReader<Ncustomer> reader;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private JdbcOperations jdbcTemplate;

    @BeforeEach
    public void setUp() {
        this.reader.setSqlSessionFactory(this.sqlSessionFactory);
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @After
    public void tearDown() {
        this.jdbcTemplate.update("delete from ncustomer");
    }

    @Test
    public void 고객정보_조회_테스트() throws Exception {
        saveNcustomer();

        Ncustomer ncustomer = reader.read();
        assertThat(ncustomer.getCustomerId()).isEqualTo(5L);
        assertThat(ncustomer.getFullName()).isEqualTo("Langelay Danette");

    }

    private void saveNcustomer() {
        jdbcTemplate.update("insert into ncustomer (customer_id, first_name, middle_name, last_name, address1, address2, city, state, postal_code, ssn, email_address, home_phone, cell_phone, work_phone, notification_pref) values (5, 'Danette', null, 'Langelay', '36 Ronald Regan Terrace', 'P.O. Box 33', 'Gaithersburg', 'Maryland', '99790', '832-86-3661', 'tlangelay4@mac.com', '240-906-7652', '907-709-2649', null, 3)");
    }

    @TestConfiguration
    public static class TestDataSourceConfig {
        private static final String CREATE_SQL =
                "CREATE TABLE ncustomer (\n" +
                "\tcustomer_id integer NOT NULL PRIMARY KEY,\n" +
                "\tfirst_name varchar(45) NOT NULL,\n" +
                "\tmiddle_name varchar(45) NULL,\n" +
                "\tlast_name varchar(45) NOT NULL,\n" +
                "\taddress1 varchar(255) NOT NULL,\n" +
                "\taddress2 varchar(255) NULL,\n" +
                "\tcity varchar(50) NOT NULL,\n" +
                "\tstate varchar(20) NOT NULL,\n" +
                "\tpostal_code varchar(5) NOT NULL,\n" +
                "\tssn varchar(11) NOT NULL,\n" +
                "\temail_address varchar(255) NULL,\n" +
                "\thome_phone varchar(12) NULL,\n" +
                "\tcell_phone varchar(12) NULL,\n" +
                "\twork_phone varchar(12) NULL,\n" +
                "\tnotification_pref varchar(1) NOT NULL,\n" +
                "\tCONSTRAINT ncustomer_pkey PRIMARY KEY (customer_id)\n" +
                ");";

        @Bean
        public DataSource dataSource() {
           return DataSourceBuilder.create()
                   .type(HikariDataSource.class)
                    .driverClassName("org.h2.Driver")
                    .url("jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE")
                    .username("SA")
                    .password("")
                    .build();
        }

        @Bean
        public DataSourceInitializer initializer(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);

            Resource create = new ByteArrayResource(CREATE_SQL.getBytes());
            dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(create));

            return dataSourceInitializer;
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:**/mapper/*.xml"));
            return sqlSessionFactoryBean.getObject();
        }

    }
}