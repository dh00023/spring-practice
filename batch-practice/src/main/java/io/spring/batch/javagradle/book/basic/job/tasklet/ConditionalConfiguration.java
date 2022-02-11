package io.spring.batch.javagradle.book.basic.job.tasklet;

import io.spring.batch.javagradle.book.basic.decider.RandomDecider;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ConditionalConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Tasklet passTasklet() {
        return (contribution, chunkContext) -> {
           return RepeatStatus.FINISHED;
           // throw new
        };
    }

    @Bean
    public Tasklet successTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("success");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet failTaklet() {
        return (contribution, chunkContext) -> {
            System.out.println("failure");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job conditionalJob() {
        return this.jobBuilderFactory.get("conditionalJob")
                .start(firstStep())
                .on("FAILED").end()
                .next(decider())
                .on("FAILED").to(failureStep())
                .from(decider()).on("*").to(successStep())
                .end()
                .build();
    }

    private JobExecutionDecider decider() {
        return new RandomDecider();
    }

    @Bean
    public Step firstStep() {
        return this.stepBuilderFactory.get("firstStep")
                .tasklet(passTasklet())
                .build();
    }

    @Bean
    public Step successStep() {
        return this.stepBuilderFactory.get("successStep")
                .tasklet(successTasklet())
                .build();
    }


    @Bean
    public Step failureStep() {
        return this.stepBuilderFactory.get("failureStep")
                .tasklet(failTaklet())
                .build();
    }
}
