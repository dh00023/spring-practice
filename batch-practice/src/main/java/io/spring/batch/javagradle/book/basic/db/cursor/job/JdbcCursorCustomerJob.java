package io.spring.batch.javagradle.book.basic.db.cursor.job;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * --job.name=jdbcCursorItemReaderJob city=Chicago
 */
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(name = "spring.batch.job.name", havingValue = "jdbcCursorItemReaderJob")
public class JdbcCursorCustomerJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job job(){
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep(){
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
                .<Customer, Customer>chunk(10)
                .reader(customerJdbcCursorItemReader())
                .writer(customerJdbcCursorItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Customer> customerJdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("customerJdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("select * from customer where city = ?")
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .preparedStatementSetter(citySetter(null))      // ???????????? ??????
                .build();
    }

    /**
     * ??????????????? SQL?????? ??????
     * ArgumentPreparedStatementSetter??? ?????? ????????? ?????? ???????????? ???? ????????? ????????? ??????
     * @param city
     * @return
     */
    @Bean
    @StepScope
    public ArgumentPreparedStatementSetter citySetter(@Value("#{jobParameters['city']}") String city) {
        return new ArgumentPreparedStatementSetter(new Object[]{city});
    }

    @Bean
    public ItemWriter customerJdbcCursorItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
