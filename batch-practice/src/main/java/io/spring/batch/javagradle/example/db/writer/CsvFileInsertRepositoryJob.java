package io.spring.batch.javagradle.example.db.writer;

import io.spring.batch.javagradle.example.db.common.domain.Customer;
import io.spring.batch.javagradle.example.db.common.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
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
 * --job.name=csvFileToRepositoryJob customerFile=/input/customer2.csv
 */
@RequiredArgsConstructor
@Configuration
public class CsvFileInsertRepositoryJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final CustomerRepository customerRepository;


    @Bean
    public Job csvFileToRepositoryJob() {
        return jobBuilderFactory.get("csvFileToRepositoryJob")
                .start(csvFileToRepositoryStep())
                .build();
    }

    @Bean
    public Step csvFileToRepositoryStep() {
        return stepBuilderFactory.get("csvFileToRepositoryStep")
                .<Customer, Customer>chunk(5)
                .reader(customerFlatFileRepositoryItemReader(null))
                .writer(repositoryItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFlatFileRepositoryItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFlatFileItemReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode"})
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .build();
    }

    @Bean
    public RepositoryItemWriter<Customer> repositoryItemWriter() {

        return new RepositoryItemWriterBuilder<Customer>()
                .repository(customerRepository)
                .methodName("save")
                .build();
    }

}
