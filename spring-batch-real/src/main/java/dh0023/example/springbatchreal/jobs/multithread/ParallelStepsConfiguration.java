package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.common.incremeter.UniqueRunIdIncrementer;
import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.config.db.PostgreBaseConfig;
import dh0023.example.springbatchreal.jobs.entity.Naccount;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static dh0023.example.springbatchreal.jobs.multithread.ParallelStepsConfiguration.JOB_NAME;


@Slf4j
@Import(SpringBatchConfigurer.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class ParallelStepsConfiguration {
    public static final String JOB_NAME = "parallelSteps";

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
        // 쓰레드 풀을 이용한 쓰레드 관리 방식
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 풀의 기본 사이즈
        executor.setMaxPoolSize(5); // 풀의 최대 사이즈
        executor.setThreadNamePrefix("multi-thread-");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);

        // allowCoreThreadTimeOut을 true로 설정해
        // core thread 가 일정시간 태스크를 받지 않을 경우 pool 에서 정리하고,
        // 모든 자식 스레드가 정리되면 jvm 도 종료 되게 설정한다.
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);

        executor.initialize();
        return executor;
    }


    @Bean(JOB_NAME)
    public Job job() {
        Flow flow = new FlowBuilder<Flow>("flow1")
                .start(step())
                .build();

        Flow flow2 = new FlowBuilder<Flow>("flow2")
                .start(accountStep())
                .build();

        Flow parelleFlow = new FlowBuilder<Flow>("parelleFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow, flow2)
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
    public Step accountStep() {
        return this.stepBuilderFactory.get(JOB_NAME + "AccountStep")
                .<Naccount, Naccount>chunk(chunkSize)
                .reader(jpaCursorItemReader())
                .writer(naccountItemWriter())
                .build();
    }

    @Bean(JOB_NAME + "NcustomerReader")
    public JdbcCursorItemReader<Ncustomer> reader() {

        String sql = "SELECT N.CUSTOMER_ID" +
                ", CONCAT(N.LAST_NAME, ' ', N.FIRST_NAME) AS FULL_NAME\n" +
                " , N.ADDRESS1 AS ADDRESS " +
                ", N.POSTAL_CODE " +
                "FROM spring.\"ncustomer\" N " +
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
        log.info("start customer writer");
        return (items) -> items.forEach(System.out::println);
    }

    @Bean(JOB_NAME + "AccountWriter")
    public ItemWriter<Naccount> naccountItemWriter() {
        log.debug("start account writer");
        return (items) -> items.forEach(System.out::println);
    }


}
