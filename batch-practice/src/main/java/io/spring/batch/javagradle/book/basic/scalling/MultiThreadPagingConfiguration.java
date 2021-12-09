package io.spring.batch.javagradle.book.basic.scalling;

import io.spring.batch.javagradle.book.basic.configurer.BasicBatchConfigurer;
import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import io.spring.batch.javagradle.book.example.total.domain.Ncustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
@Import(BasicBatchConfigurer.class)
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "multiThreadPaging")
public class MultiThreadPagingConfiguration {

    private static final String JOB_NAME = "multiThreadPaging";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize;
    private int poolSize;

    /**
     * setter로 입력 받는 이유는 spring Context없이 테스트 코드 작성할때
     * poolSize, chunkSize를 알 수 있는 방법이 없음.
     */

    @Value("${chunkSize:100}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Value("${poolSize:5}")
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Bean(JOB_NAME)
    public Job job() {
        return this.jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(step())
//                .preventRestart()
                .build();

    }

    @Bean(JOB_NAME + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(JOB_NAME + "Step")
                .<Customer, Customer> chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .taskExecutor(executor())
                .throttleLimit(poolSize) // default : 4, 생성된 쓰레드 중 몇개를 실제 작업에 사용할지 결정
                .build();
    }

    @Bean(JOB_NAME + "TaskPool")
    public TaskExecutor executor() {
        // 쓰레드 풀을 이용한 쓰레드 관리 방식
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize); // 풀의 기본 사이즈
        executor.setMaxPoolSize(poolSize); // 풀의 최대 사이즈
        executor.setThreadGroupName("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean(value = JOB_NAME + "Reader", destroyMethod = "")
    public JpaPagingItemReader<Customer> reader() {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name(JOB_NAME + "Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("select c from Customer c")
                .saveState(false) // 쓰레드는 상태 보장이 안되므로 저장하지 않음
                .build();
    }

    @Bean(JOB_NAME + "Writer")
    public ItemWriter<Customer> writer() {
        log.debug("start writer");
        return (items) -> items.forEach(System.out::println);
    }

}
