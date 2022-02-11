package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.config.TestBatchConfig;
import dh0023.example.springbatchreal.config.TestDataSourceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@ContextConfiguration(classes = {
        TestBatchConfig.class
        , MultithreadPagingConfiguration.class
        , TestDataSourceConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + MultithreadPagingConfiguration.JOB_NAME, "poolSize=5", "chunkSize=10"})
class MultithreadPagingConfigurationTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @AfterEach
    void tearDown() {
        this.jdbcTemplate.update("delete from NCUSTOMER");
    }

    @Test
    public void ITEM_PAGING_READER_병렬수행_테스트() throws Exception {
        for (int i = 1; i <= 60; i++) {
            saveNcustomer(i);
        }

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(stepExecution.getReadCount()).isEqualTo(60);
    }

    private void saveNcustomer(int i) {
        jdbcTemplate.update("insert into ncustomer (customer_id, first_name, middle_name, last_name, address1, address2, city, state, postal_code, ssn, email_address, home_phone, cell_phone, work_phone, notification_pref) values (?, 'Danette', null, 'Langelay', '36 Ronald Regan Terrace', 'P.O. Box 33', 'Gaithersburg', 'Maryland', '99790', '832-86-3661', 'tlangelay4@mac.com', '240-906-7652', '907-709-2649', null, 3)", i);
    }
}