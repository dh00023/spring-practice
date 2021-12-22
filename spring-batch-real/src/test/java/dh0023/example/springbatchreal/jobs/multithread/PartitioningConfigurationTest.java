package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.config.TestBatchConfig;
import dh0023.example.springbatchreal.config.TestDataSourceConfig;
import dh0023.example.springbatchreal.config.TestJpaConfig;
import dh0023.example.springbatchreal.jobs.mysql.entity.Naccount;
import dh0023.example.springbatchreal.jobs.mysql.repository.NaccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(classes = {
        TestBatchConfig.class
        , PartitioningConfiguration.class
        , TestDataSourceConfig.class
        , TestJpaConfig.class})
@TestPropertySource(properties = {"spring.batch.job.names=" + PartitioningConfiguration.JOB_NAME})
class PartitioningConfigurationTest {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private NaccountRepository naccountRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @AfterEach
    public void tearDown() {
        naccountRepository.deleteAllInBatch();
    }

    @Test
    void PARTITIONING_BATCH_JOB_TEST() throws Exception {
        // given
        LocalDate testDate = LocalDate.of(2021, 12, 22);

        List<Naccount> naccounts = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            naccounts.add(new Naccount(Long.valueOf(i), BigDecimal.TEN, testDate));
        }
        naccountRepository.saveAll(naccounts);

        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParametersBuilder()
                .addString("startDate", testDate.format(FORMATTER))
                .addString("endDate", testDate.plusDays(1).format(FORMATTER))
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);


    }


}