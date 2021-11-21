package io.spring.batch.javagradle.chunk;

import io.spring.batch.javagradle.listener.LoggingStepStartStopListener;
import io.spring.batch.javagradle.policy.RandomChunkSizePolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EnableBatchProcessing
@Configuration
public class ChunkBasedConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 실제 스프링 배치 Job 생성
     */
    @Bean
    public Job chunkBasedJob() {
        return this.jobBuilderFactory.get("chunkBasedJob")
                .start(chunkStep())
                .build();
    }

    @Bean
    public Step chunkStep() {
        return this.stepBuilderFactory.get("chunkStep")
                .<String, String> chunk(randomChunkSizePolicy()) // completePolicy 호출
                .reader(itemReader())
                .writer(itemWriter())
                .listener(new LoggingStepStartStopListener())
                .build();
    }

    @Bean
    public ListItemReader<String> itemReader(){

        List<String> items = new ArrayList<>(100000);

        for (int i = 0; i < 100000; i++) {
            items.add(UUID.randomUUID().toString());
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> current item = " + item);
            }
            System.out.println(">> end itemWriter chunk " + items.size());
        };
    }

    @Bean
    public CompletionPolicy simpleCompletionPolicy() {
        // 처리된 ITEM 개수를 세어, 이 개수가 임계값에 도달하면 chunk 완료로 표시
        SimpleCompletionPolicy simpleCompletionPolicy = new SimpleCompletionPolicy(1000);

        return simpleCompletionPolicy;
    }

    @Bean
    public CompletionPolicy timeoutCompletionPolicy() {
        TimeoutTerminationPolicy timeoutTerminationPolicy = new TimeoutTerminationPolicy(3);
        return timeoutTerminationPolicy;
    }

    @Bean
    public CompletionPolicy compositeCompletionPolicy() {
        CompositeCompletionPolicy policy = new CompositeCompletionPolicy();

        // 여러 정책 설정
        policy.setPolicies(
                new CompletionPolicy[]{
                        new TimeoutTerminationPolicy(3),
                        new SimpleCompletionPolicy(1000)
                }
        );

        return policy;

    }

    @Bean
    public CompletionPolicy randomChunkSizePolicy() {
        return new RandomChunkSizePolicy();
    }
}

