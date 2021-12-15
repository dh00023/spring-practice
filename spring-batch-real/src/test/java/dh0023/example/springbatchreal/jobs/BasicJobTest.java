package dh0023.example.springbatchreal.jobs;

import dh0023.example.springbatchreal.config.TestBatchConfig;
import dh0023.example.springbatchreal.jobs.basic.BasicJob;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBatchTest  // JobLauncherTestUtils 등 배치 테스트시 필요한 빈 등록
@SpringBootTest(classes = {BasicJob.class, TestBatchConfig.class}) // 자동 설정이 많이 필요한 의존성들이 있는 프로젝트 사용
@TestPropertySource(properties = {"spring.batch.job.names=" + BasicJob.JOB_NAME})
public class BasicJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void 배치_수행_테스트() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("test", Long.valueOf(1))
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
