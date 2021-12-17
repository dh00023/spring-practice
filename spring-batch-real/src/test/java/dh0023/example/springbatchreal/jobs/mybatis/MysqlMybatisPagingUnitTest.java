package dh0023.example.springbatchreal.jobs.mybatis;

import dh0023.example.springbatchreal.config.TestDataSourceConfig;
import dh0023.example.springbatchreal.config.db.MysqlMybatisConfig;
import dh0023.example.springbatchreal.config.db.PostgreMybatisConfig;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@ContextConfiguration(classes={
        MysqlMybatisPagingConfiguration.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + MysqlMybatisPagingConfiguration.JOB_NAME})
public class MysqlMybatisPagingUnitTest {

    @Autowired
    private MyBatisPagingItemReader<Ncustomer> reader;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private JdbcOperations jdbcTemplate;

    @BeforeEach
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @AfterEach
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


    public static class TestDataSourceConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }

        @Bean
        @ConditionalOnMissingBean
        public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {

            ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
            resourceDatabasePopulator.addScript(new ClassPathResource("/schema/test.sql"));
            resourceDatabasePopulator.addScript(new ClassPathResource("/org/springframework/batch/core/schema-h2.sql"));

            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);
            dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
            return dataSourceInitializer;
        }

        @Primary
        @Bean(MysqlMybatisConfig.MAIN_MYBATIS_SQL_SEESION_FACTORY)
        public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/**/mapper/*.xml"));
            return sqlSessionFactoryBean.getObject();
        }

        @Bean(PostgreMybatisConfig.P_MYBATIS_SQL_SEESION_FACTORY)
        public SqlSessionFactory pSqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:**/mapper/*.xml"));
            return sqlSessionFactoryBean.getObject();
        }
    }
}