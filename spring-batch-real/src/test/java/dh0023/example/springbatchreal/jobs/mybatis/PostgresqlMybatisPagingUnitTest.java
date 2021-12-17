package dh0023.example.springbatchreal.jobs.mybatis;

import dh0023.example.springbatchreal.config.TestDataSourceConfig;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@ContextConfiguration(classes={
        PostgreMybatisPagingConfiguration.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + PostgreMybatisPagingConfiguration.JOB_NAME})
class PostgresqlMybatisPagingUnitTest {

    @Autowired
    private MyBatisPagingItemReader<Ncustomer> reader;

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("postgreMybatisSqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    private JdbcOperations jdbcTemplate;

    @BeforeEach
    public void setUp() {
        this.reader.setSqlSessionFactory(this.sqlSessionFactory);
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @AfterEach
    public void tearDown() {
        this.jdbcTemplate.update("delete from ncustomer");
    }

    @Test
//    @Sql("/schema/test.sql")
    public void 고객정보_조회_테스트() throws Exception {
        saveNcustomer();

        Ncustomer ncustomer = reader.read();
        assertThat(ncustomer.getCustomerId()).isEqualTo(5L);
        assertThat(ncustomer.getFullName()).isEqualTo("Langelay Danette");

    }

    private void saveNcustomer() {
        jdbcTemplate.update("insert into ncustomer (customer_id, first_name, middle_name, last_name, address1, address2, city, state, postal_code, ssn, email_address, home_phone, cell_phone, work_phone, notification_pref) values (5, 'Danette', null, 'Langelay', '36 Ronald Regan Terrace', 'P.O. Box 33', 'Gaithersburg', 'Maryland', '99790', '832-86-3661', 'tlangelay4@mac.com', '240-906-7652', '907-709-2649', null, 3)");
    }
}