package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static dh0023.example.springbatchreal.jobs.multithread.MultithreadPagingConfiguration.JOB_NAME;

@Slf4j
@Import(SpringBatchConfigurer.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class MultithreadPagingConfiguration {

    public static final String JOB_NAME = "multithreadPagingJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

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
                .<Ncustomer, Ncustomer> chunk(chunkSize)
                .reader(reader(null))
                .writer(writer())
                .taskExecutor(executor())
                .throttleLimit(poolSize) // default : 4, ????????? ????????? ??? ????????? ?????? ????????? ???????????? ??????
                .build();
    }

    @Bean(JOB_NAME + "TaskPool")
    public TaskExecutor executor() {
        // ????????? ?????? ????????? ????????? ?????? ??????
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize); // ?????? ?????? ?????????
        executor.setMaxPoolSize(poolSize); // ?????? ?????? ?????????
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

    @Bean(JOB_NAME + "Reader")
    public JdbcPagingItemReader<Ncustomer> reader(PagingQueryProvider pagingQueryProvider) {

        return new JdbcPagingItemReaderBuilder<Ncustomer>()
                .name("customerJdbcPagingItemReader")   // Reader??? ??????, ExecutionContext??? ??????????????? ??????
                .dataSource(dataSource)                 // DB??? ???????????? ?????? ????????? DataSource??????
                .queryProvider(pagingQueryProvider)     // PagingQueryProvider
                .pageSize(10)                           // ??? ????????? ??????
                .rowMapper(new BeanPropertyRowMapper<>(Ncustomer.class)) // ?????? ????????? ??????????????? ???????????? ?????? ??????
                .saveState(false)                       // Reader??? ????????? ????????? ???????????? ????????? ??????
                .build();
    }

    @Bean(JOB_NAME + "Writer")
    public ItemWriter<Ncustomer> writer() {
        log.debug("start writer");
        return (items) -> items.forEach(System.out::println);
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProvider(){
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource); // ????????? ????????????????????? ????????? ??????(setDatabaseType ?????? ?????????????????? ?????? ????????? ??????)
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from ncustomer");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("customer_id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider;
    }

}
