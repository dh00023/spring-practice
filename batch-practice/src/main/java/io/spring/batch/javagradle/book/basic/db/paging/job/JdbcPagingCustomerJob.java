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
                .name("customerJdbcPagingItemReader")   // Reader??? ??????, ExecutionContext??? ??????????????? ??????
                .dataSource(dataSource)                 // DB??? ???????????? ?????? ????????? DataSource??????
                .queryProvider(pagingQueryProvider)     // PagingQueryProvider
                .parameterValues(params)                // SQL ?????? ??????????????? ????????????
                .pageSize(10)                           // ??? ????????? ??????
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class)) // ?????? ????????? ??????????????? ???????????? ?????? ??????
                .build();
    }

    @Bean
    public ItemWriter customerJdbcPagingItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }


    @Bean
    public SqlPagingQueryProviderFactoryBean pagingQueryProvider(){
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource); // ????????? ????????????????????? ????????? ??????(setDatabaseType ?????? ?????????????????? ?????? ????????? ??????)
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where city = :city");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("lastName", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider;
    }
}
