package io.spring.batch.javagradle.basic.job.tasklet;

import io.spring.batch.javagradle.basic.tasklet.HelloWorld;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 실제 스프링 배치 Job 생성
     */
    @Bean
    public Job simpleJob() {
        return this.jobBuilderFactory.get("simpleJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return this.stepBuilderFactory.get("step")
                .tasklet(helloWorldTasklet())
                .build();
    }

    @Bean
    public Tasklet helloWorldTasklet() {
        return new HelloWorld();
    }
}
