package dh0023.example.springbatchreal.jobs.mybatis;


import dh0023.example.springbatchreal.common.incremeter.UniqueRunIdIncrementer;
import dh0023.example.springbatchreal.config.SpringBatchConfigurer;
import dh0023.example.springbatchreal.config.db.PostgreMybatisConfig;
import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static dh0023.example.springbatchreal.jobs.mybatis.PostgreMybatisPagingConfiguration.JOB_NAME;

@Configuration
@RequiredArgsConstructor
@Import(SpringBatchConfigurer.class)
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = JOB_NAME)
public class PostgreMybatisPagingConfiguration {

    public static final String JOB_NAME = "postgreMybatisPagingJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier(PostgreMybatisConfig.P_MYBATIS_SQL_SEESION_FACTORY)
    private SqlSessionFactory sqlSessionFactory;

    private int chunkSize;

    @Value("${chunkSize:100}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * 실제 스프링 배치 Job 생성
     */
    @Bean(JOB_NAME)
    public Job job() {
        return this.jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .incrementer(new UniqueRunIdIncrementer())
                .build();
    }
    /**
     * 실제 스프링 배치 step 생성
     *
     * @return
     */
    @Bean(JOB_NAME+"Step")
    public Step step() {
        return this.stepBuilderFactory.get(JOB_NAME+"Step")
                .<Ncustomer, Ncustomer> chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean(JOB_NAME+ "Reader")
    public MyBatisPagingItemReader<Ncustomer> reader() {
        return new MyBatisPagingItemReaderBuilder<Ncustomer>()
                .pageSize(chunkSize)
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("dh0023.example.springbatchreal.jobs.mybatis.mapper.getNcustomer")
//                .parameterValues()
                .build()
                ;
    }

    @Bean(JOB_NAME+"Writer")
    public ItemWriter<Ncustomer> writer() {
        return items -> items.forEach(System.out::println);
    }

}
