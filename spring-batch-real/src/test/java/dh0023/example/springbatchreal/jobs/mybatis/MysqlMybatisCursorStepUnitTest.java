package dh0023.example.springbatchreal.jobs.mybatis;

import dh0023.example.springbatchreal.config.TestBatchConfig;
import dh0023.example.springbatchreal.config.TestDataSourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@ContextConfiguration(classes={
        MysqlMybatisCursorConfiguration.class,
        TestBatchConfig.class,
        TestDataSourceConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + MysqlMybatisCursorConfiguration.JOB_NAME})
public class MysqlMybatisCursorStepUnitTest  {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DataSource dataSource;

    private JdbcOperations jdbcTemplate;

    @BeforeEach
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AfterEach
    public void tearDown() {
        this.jdbcTemplate.update("delete from ncustomer");
    }


    @Test
    public void 고객정보_조회_테스트() throws Exception {
        saveNcustomer();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(MysqlMybatisCursorConfiguration.JOB_NAME + "Step");
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    private void saveNcustomer() {
        jdbcTemplate.update("insert into ncustomer (customer_id, first_name, middle_name, last_name, address1, address2, city, state, postal_code, ssn, email_address, home_phone, cell_phone, work_phone, notification_pref) values (5, 'Danette', null, 'Langelay', '36 Ronald Regan Terrace', 'P.O. Box 33', 'Gaithersburg', 'Maryland', '99790', '832-86-3661', 'tlangelay4@mac.com', '240-906-7652', '907-709-2649', null, 3)");
    }
}