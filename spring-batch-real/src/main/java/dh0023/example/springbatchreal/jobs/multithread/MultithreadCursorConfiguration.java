package dh0023.example.springbatchreal.jobs.multithread;

import dh0023.example.springbatchreal.common.incremeter.UniqueRunIdIncrementer;
import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.jobs.multithread.listener.CursorItemReaderListener;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
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

import static dh0023.example.springbatchreal.jobs.multithread.MultithreadCursorConfiguration.JOB_NAME;

@Slf4j
@Import(SpringBatchConfigurer.class)
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class MultithreadCursorConfiguration {

    public static final String JOB_NAME = "multithreadCursorJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    private int chunkSize;
    private int poolSize;
    /**
     * setter로 입력 받는 이유는 spring Context없이 테스트 코드 작성할때
     * poolSize, chunkSize를 알 수 있는 방법이 없음.
     */

    @Value("${chunkSize:10}")
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
                .incrementer(new UniqueRunIdIncrementer())
                .start(step())
//                .preventRestart()
                .build();

    }

    @Bean(JOB_NAME + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(JOB_NAME + "Step")
                .<Ncustomer, Ncustomer> chunk(chunkSize)
                .reader(reader())
                .listener(new CursorItemReaderListener()) // cursorItemReader는 데이터 읽기시 별도 로그를 남기지 않아 listener추가
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

    @Bean(JOB_NAME + "Reader")
    public SynchronizedItemStreamReader<Ncustomer> reader() {

        String sql = "SELECT N.CUSTOMER_ID" +
                ", CONCAT(N.LAST_NAME, ' ', N.FIRST_NAME) AS FULL_NAME\n" +
                " , N.ADDRESS1 AS ADDRESS\n" +
                ", N.POSTAL_CODE\n" +
                "FROM NCUSTOMER N\n" +
                "ORDER BY CUSTOMER_ID " +
                "LIMIT 55";
        log.info(sql);

        JdbcCursorItemReader itemReader =  new JdbcCursorItemReaderBuilder<Ncustomer>()
                .name(JOB_NAME + "Reader")   // Reader의 이름, ExecutionContext에 저장되어질 이름
                .dataSource(dataSource)                 // DB에 접근하기 위해 사용할 DataSource객체
                .rowMapper(new BeanPropertyRowMapper<>(Ncustomer.class)) // 쿼리 결과를 인스턴스로 매핑하기 위한 매퍼
                .sql(sql)
                .saveState(false)                       // Reader가 실패한 지점을 저장하지 않도록 설정
                .build();

        return new SynchronizedItemStreamReaderBuilder<Ncustomer>()
                .delegate(itemReader)
                .build();
    }

    @Bean(JOB_NAME + "Writer")
    public ItemWriter<Ncustomer> writer() {
        log.debug("start writer");
        return (items) -> items.forEach(System.out::println);
    }
}
