package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.common.incremeter.UniqueRunIdIncrementer;
import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
                .start(step())
                .incrementer(new UniqueRunIdIncrementer())
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

    @Bean(JOB_NAME + "Reader")
    public JdbcPagingItemReader<Ncustomer> reader(PagingQueryProvider pagingQueryProvider) {

        return new JdbcPagingItemReaderBuilder<Ncustomer>()
                .name("customerJdbcPagingItemReader")   // Reader의 이름, ExecutionContext에 저장되어질 이름
                .dataSource(dataSource)                 // DB에 접근하기 위해 사용할 DataSource객체
                .queryProvider(pagingQueryProvider)     // PagingQueryProvider
                .pageSize(10)                           // 각 페이지 크기
                .rowMapper(new BeanPropertyRowMapper<>(Ncustomer.class)) // 쿼리 결과를 인스턴스로 매핑하기 위한 매퍼
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
        queryProvider.setDataSource(dataSource); // 제공된 데이터베이스의 타입을 결정(setDatabaseType 으로 데이터베이스 타입 설정도 가능)
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from ncustomer");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("customer_id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider;
    }

}
