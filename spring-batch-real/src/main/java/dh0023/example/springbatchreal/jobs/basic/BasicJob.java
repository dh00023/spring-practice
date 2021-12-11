package dh0023.example.springbatchreal.jobs.basic;


import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static dh0023.example.springbatchreal.jobs.basic.BasicJob.JOB_NAME;

@Import(SpringBatchConfigurer.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class BasicJob {

    public static final String JOB_NAME = "basic";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * 실제 스프링 배치 Job 생성
     */
    @Bean(JOB_NAME)
    public Job job() {
        return this.jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }
    /**
     * 실제 스프링 배치 step 생성
     *
     * @return
     */
    @Bean(JOB_NAME+"Step")
    public Step step() {
        return this.stepBuilderFactory.get(JOB_NAME+"Step")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Hello, world!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
