package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.config.TestBatchConfig;
import dh0023.example.springbatchreal.config.TestDataSourceConfig;
import dh0023.example.springbatchreal.config.TestJpaConfig;
import dh0023.example.springbatchreal.config.db.PostgreBaseConfig;
import dh0023.example.springbatchreal.jobs.mysql.entity.Naccount;
import dh0023.example.springbatchreal.jobs.mysql.repository.NaccountRepository;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@ContextConfiguration(classes = {
        TestBatchConfig.class
        , ParallelStepsConfiguration.class
        , TestDataSourceConfig.class
        , TestJpaConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + ParallelStepsConfiguration.JOB_NAME})
class ParallelStepsConfigurationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private NaccountRepository naccountRepository;

    @Autowired
    @Qualifier(PostgreBaseConfig.POSTGRE_READER_DATASOURCE)
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @AfterEach
    void tearDown() {
        naccountRepository.deleteAllInBatch();
        this.jdbcTemplate.update("delete from NCUSTOMER");
    }
    @Test
    public void PARALLEL_STEP_????????????_?????????() throws Exception {
        // given
        LocalDate testDate = LocalDate.of(2021, 12, 22);

        List<Naccount> naccounts = new ArrayList<>();

        for (int i = 1; i <= 60; i++) {
            naccounts.add(new Naccount(Long.valueOf(i), BigDecimal.TEN, testDate));
        }
        naccountRepository.saveAll(naccounts);

        for (int i = 1; i <= 60; i++) {
            saveNcustomer(i);
        }

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getStepExecutions().size()).isEqualTo(3);

        Iterator<StepExecution> iterator = jobExecution.getStepExecutions().iterator();

        StepExecution step1 = iterator.next();
        assertThat(step1.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(step1.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution step2 = iterator.next();
        assertThat(step2.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(step2.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution step3 = iterator.next();
        assertThat(step3.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(step3.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    private void saveNcustomer(int i) {
        jdbcTemplate.update("insert into ncustomer (customer_id, first_name, middle_name, last_name, address1, address2, city, state, postal_code, ssn, email_address, home_phone, cell_phone, work_phone, notification_pref) values (?, 'Danette', null, 'Langelay', '36 Ronald Regan Terrace', 'P.O. Box 33', 'Gaithersburg', 'Maryland', '99790', '832-86-3661', 'tlangelay4@mac.com', '240-906-7652', '907-709-2649', null, 3)", i);
    }

}