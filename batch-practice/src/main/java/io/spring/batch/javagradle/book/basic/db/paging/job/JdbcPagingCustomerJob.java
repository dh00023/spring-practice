package io.spring.batch.javagradle.book.basic.db.paging.job;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * --job.name=jdbcPagingItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
public class JdbcPagingCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingItemReaderJob(){
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
                .start(jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep(){
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerJdbcPagingItemReader(null, null))
                .writer(customerJdbcPagingItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> customerJdbcPagingItemReader(
            PagingQueryProvider pagingQueryProvider, @Value("#{jobParameters['city']}") String city) {

        Map<String, Object> params = new HashMap<>(1);
        params.put("city", city);


        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("customerJdbcPagingItemReader")   // Reader의 이름, ExecutionContext에 저장되어질 이름
                .dataSource(dataSource)                 // DB에 접근하기 위해 사용할 DataSource객체
                .queryProvider(pagingQueryProvider)     // PagingQueryProvider
                .parameterValues(params)                // SQL 문에 주입해야할 파라미터
                .pageSize(10)                           // 각 페이지 크기
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class)) // 쿼리 결과를 인스턴스로 매핑하기 위한 매퍼
                .build();
    }

    @Bean
    public ItemWriter customerJdbcPagingItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }


    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProvider(){
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource); // 제공된 데이터베이스의 타입을 결정(setDatabaseType 으로 데이터베이스 타입 설정도 가능)
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where city = :city");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("lastName", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider;
    }
}
