package io.spring.batch.javagradle.book.basic.db.writer;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import io.spring.batch.javagradle.book.basic.db.common.statement.CustomerItemPreparedStatementSetter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

/**
 * --job.name=csvFileInsertDbJob customerFile=/input/customer2.csv
 */
@RequiredArgsConstructor
@Configuration
public class CsvFileInsertJdbcJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job csvFileInsertDbJob() {
        return jobBuilderFactory.get("csvFileInsertDbJob")
                .start(csvFileInsertDbStep())
                .build();
    }

    @Bean
    public Step csvFileInsertDbStep() {
        return stepBuilderFactory.get("csvFileInsertDbStep")
                .<Customer, Customer>chunk(5)
                .reader(customerFlatFileItemReader(null))
                .writer(customerJdbcBatchItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFlatFileItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFlatFileItemReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode"})
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> customerJdbcBatchPSItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO CUSTOMER (first_name, middle_initial, last_name, address, city, state, zip_code)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")
                .itemPreparedStatementSetter(new CustomerItemPreparedStatementSetter())
                .build()
                ;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> customerJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("INSERT INTO CUSTOMER (first_name, middle_initial, last_name, address, city, state, zip_code)" +
                        "VALUES (:firstName, :middleInitial, :lastName, :address, :city, :state, :zipCode)")
                .beanMapped()
                .build()
                ;
    }
}
