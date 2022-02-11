package io.spring.batch.javagradle.book.basic.job.tasklet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Callable;

@Configuration
@RequiredArgsConstructor
public class CallableTaskletConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job callableJob() {
        return this.jobBuilderFactory.get("callableJob")
                .start(callableStep())
                .build();
    }

    @Bean
    public Step callableStep() {
        return this.stepBuilderFactory.get("callableStep")
                .tasklet(callableTasklet())
                .build();
    }

    @Bean
    public Callable<RepeatStatus> callableObject() {
        return () -> {
            System.out.println("This was executed in another thread");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public CallableTaskletAdapter callableTasklet() {

        //CallableTaskletAdapter는 Step이 실행되는 스레드와 별개의 스레드에서 실행되지만
        // Step과 병렬로 실행되는 것은 아니다.
        CallableTaskletAdapter callableTaskletAdapter = new CallableTaskletAdapter();

        callableTaskletAdapter.setCallable(callableObject());

        return callableTaskletAdapter;
    }

}
