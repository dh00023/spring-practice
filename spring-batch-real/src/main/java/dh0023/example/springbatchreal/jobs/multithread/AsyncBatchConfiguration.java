package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.common.incremeter.UniqueRunIdIncrementer;
import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.config.db.PostgreBaseConfig;
import dh0023.example.springbatchreal.jobs.mysql.entity.Naccount;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.concurrent.Future;

import static dh0023.example.springbatchreal.jobs.multithread.AsyncBatchConfiguration.JOB_NAME;

@Slf4j
@Import(SpringBatchConfigurer.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class AsyncBatchConfiguration {

    public static final String JOB_NAME = "asyncBatchJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    @Qualifier(PostgreBaseConfig.POSTGRE_READER_DATASOURCE)
    private DataSource dataSource;

    @Value("${chunkSize:10}")
    private int chunkSize;

    @Bean(JOB_NAME + "TaskPool")
    public TaskExecutor executor() {
        // ????????? ?????? ????????? ????????? ?????? ??????
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // ?????? ?????? ?????????
        executor.setMaxPoolSize(10); // ?????? ?????? ?????????
        executor.setThreadNamePrefix("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);

        // allowCoreThreadTimeOut??? true??? ?????????
        // core thread ??? ???????????? ???????????? ?????? ?????? ?????? pool ?????? ????????????,
        // ?????? ?????? ???????????? ???????????? jvm ??? ?????? ?????? ????????????.
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);

        executor.initialize();
        return executor;
    }


    @Bean(JOB_NAME)
    public Job job() {
        Flow flow1 = new FlowBuilder<Flow>("flow1")
                .start(step())
                .build();

        Flow flow2 = new FlowBuilder<Flow>("flow2")
                .start(step2())
                .build();


        Flow parelleFlow = new FlowBuilder<Flow>("parelleFlow")
                .split(executor())
                .add(flow1, flow2)
                .build();

        return this.jobBuilderFactory.get(JOB_NAME)
                .incrementer(new UniqueRunIdIncrementer())
                .start(parelleFlow)
                .end()
                .build();
    }

    @Bean(JOB_NAME + "CustomerStep")
    public Step step() {
        return this.stepBuilderFactory.get(JOB_NAME + "CustomerStep")
                .<Ncustomer, Ncustomer>chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .build();
    }


    @Bean(JOB_NAME + "AccountStep")
    public Step step2() {
        return this.stepBuilderFactory.get(JOB_NAME + "AccountStep")
                .<Naccount, Future<Naccount>>chunk(chunkSize)
                .reader(jpaCursorItemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean(JOB_NAME + "NcustomerReader")
    public JdbcCursorItemReader<Ncustomer> reader() {

        String sql = "SELECT N.CUSTOMER_ID" +
                ", CONCAT(N.LAST_NAME, ' ', N.FIRST_NAME) AS FULL_NAME\n" +
                " , N.ADDRESS1 AS ADDRESS " +
                ", N.POSTAL_CODE " +
                "FROM ncustomer N " +
                "ORDER BY CUSTOMER_ID";
        log.info(sql);

        return new JdbcCursorItemReaderBuilder<Ncustomer>()
                .name(JOB_NAME + "Reader")
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Ncustomer.class))
                .sql(sql)
                .saveState(false)
                .build();
    }

    @Bean(JOB_NAME + "NaccountReader")
    public JpaCursorItemReader<Naccount> jpaCursorItemReader() {
        return new JpaCursorItemReaderBuilder<Naccount>()
                .name(JOB_NAME + "NaccountReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select n from Naccount n")
                .build();
    }

    @Bean(JOB_NAME + "Writer")
    public ItemWriter<Ncustomer> writer() {
        return (items) -> items.forEach(System.out::println);
    }


    @Bean
    public AsyncItemProcessor<Naccount, Naccount> asyncItemProcessor() {
        AsyncItemProcessor<Naccount, Naccount> processor = new AsyncItemProcessor<>();
        processor.setDelegate(processor());
        processor.setTaskExecutor(executor());
        return processor;
    }

    @Bean(JOB_NAME + "AccountProcessor")
    public ItemProcessor<Naccount, Naccount> processor() {
        return (account)->{
            Thread.sleep(5);; // ????????? ?????? ?????? ????????? ??????
            return account;
        };
    }

    @Bean
    public AsyncItemWriter<Naccount> asyncItemWriter() {
        AsyncItemWriter<Naccount> writer = new AsyncItemWriter<>();
        writer.setDelegate(naccountItemWriter());
        return writer;
    }
    @Bean(JOB_NAME + "AccountWriter")
    public ItemWriter<Naccount> naccountItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }


}
