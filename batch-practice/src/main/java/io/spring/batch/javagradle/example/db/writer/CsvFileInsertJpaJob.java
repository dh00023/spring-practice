package io.spring.batch.javagradle.example.db.writer;

import io.spring.batch.javagradle.example.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

/**
 * --job.name=csvFileToJpaJob customerFile=/input/customer2.csv
 */
@RequiredArgsConstructor
@Configuration
public class CsvFileInsertJpaJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public Job csvFileToJpaJob() {
        return jobBuilderFactory.get("csvFileToJpaJob")
                .start(csvFileToJpaStep())
                .build();
    }

    @Bean
    public Step csvFileToJpaStep() {
        return stepBuilderFactory.get("csvFileToJpaStep")
                .<Customer, Customer>chunk(5)
                .reader(customerFlatFileJpaItemReader(null))
                .writer(jpaItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFlatFileJpaItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFlatFileItemReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode"})
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .build();
    }

    @Bean
    public JpaItemWriter<Customer> jpaItemWriter() {

        return new JpaItemWriterBuilder<Customer>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
