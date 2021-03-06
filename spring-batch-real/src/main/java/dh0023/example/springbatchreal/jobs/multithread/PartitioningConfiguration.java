package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.jobs.multithread.partitioner.AccountIdRangePartitioner;
import dh0023.example.springbatchreal.jobs.mysql.entity.Naccount;
import dh0023.example.springbatchreal.jobs.mysql.repository.NaccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static dh0023.example.springbatchreal.jobs.multithread.PartitioningConfiguration.JOB_NAME;

@Slf4j
@Import(SpringBatchConfigurer.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class PartitioningConfiguration {

    public static final String JOB_NAME = "partitioningExample";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final NaccountRepository naccountRepository;

    private int chunkSize;
    private int poolSize;

    /**
     * setter??? ?????? ?????? ????????? spring Context?????? ????????? ?????? ????????????
     * poolSize, chunkSize??? ??? ??? ?????? ????????? ??????.
     */

    @Value("${chunkSize:100}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Value("${poolSize:5}")
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }


    @Bean(JOB_NAME + "TaskPool")
    public TaskExecutor executor() {
        // ????????? ?????? ????????? ????????? ?????? ??????
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize); // ?????? ?????? ?????????
        executor.setMaxPoolSize(poolSize); // ?????? ?????? ?????????
        executor.setThreadNamePrefix("partition-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);

        // allowCoreThreadTimeOut??? true??? ?????????
        // core thread ??? ???????????? ???????????? ?????? ?????? ?????? pool ?????? ????????????,
        // ?????? ?????? ???????????? ???????????? jvm ??? ?????? ?????? ????????????.
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);

        executor.initialize();
        return executor;
    }


    @Bean(name = JOB_NAME + "PartitionHandler")
    public TaskExecutorPartitionHandler partitionHandler() {
        // ???????????? ?????????????????? ???????????? ??????
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();

        // worker??? ????????? step
        partitionHandler.setStep(step());

        // ??????????????? ????????? ?????? TaskExecutor??????
        partitionHandler.setTaskExecutor(executor());

        // ????????? ????????? gridSize??? ????????????.
        partitionHandler.setGridSize(poolSize);

        return partitionHandler;
    }

    @Bean(JOB_NAME)
    public Job job() {
        return this.jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(stepManager())
//                .preventRestart()
                .build();

    }

    @Bean(JOB_NAME + "StepManager")
    public Step stepManager() {
        // ???????????? ?????? step??? ????????? ???????????? ????????????. (???????????? step??? ?????? ?????? ????????????)
        return this.stepBuilderFactory.get("step.manager")
                .partitioner("step", partitioner(null, null)) // Partitioner ????????? ??????
                .step(step()) // ??????????????? Step??????
                .partitionHandler(partitionHandler()) // PartitionHandler ??????
                .build();
    }

    @Bean(name = JOB_NAME + "Partitioner")
    @StepScope
    public AccountIdRangePartitioner partitioner(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate) {
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return new AccountIdRangePartitioner(naccountRepository, startLocalDate, endLocalDate);
    }

    @Bean(JOB_NAME + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(JOB_NAME + "Step")
                .<Naccount, Naccount>chunk(chunkSize)
                .reader(reader(null, null))
                .writer(writer(null, null))
                .build();
    }

    /**
     * AccountIdRangePartitioner?????? ????????? ??? ???????????? StepExecution ????????? ????????? ??????
     * @param minId
     * @param maxId
     * @return
     */
    @Bean(JOB_NAME + "Reader")
    @StepScope
    public JpaPagingItemReader<Naccount> reader(@Value("#{stepExecutionContext[minId]}") Long minId,
                                                @Value("#{stepExecutionContext[maxId]}") Long maxId) {

        Map<String, Object> params = new HashMap<>();
        params.put("minId", minId);
        params.put("maxId", maxId);

        log.info("reader minId={}, maxId={}", minId, maxId);

        return new JpaPagingItemReaderBuilder<Naccount>()
                .name(JOB_NAME + "Reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT n FROM Naccount n WHERE n.accountId BETWEEN :minId AND :maxId")
                .parameterValues(params)
                .build();
    }

    @Bean(JOB_NAME + "Writer")
    @StepScope
    public ItemWriter<Naccount> writer(@Value("#{stepExecutionContext[minId]}") Long minId,
                                       @Value("#{stepExecutionContext[maxId]}") Long maxId) {
        log.info("minId : {}, maxId: {}", minId, maxId);
        return (items) -> items.forEach(System.out::println);
    }
}
