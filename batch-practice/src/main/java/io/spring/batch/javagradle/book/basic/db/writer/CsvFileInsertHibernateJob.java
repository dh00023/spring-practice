package io.spring.batch.javagradle.book.basic.db.writer;

import io.spring.batch.javagradle.book.basic.db.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

/**
 * --job.name=csvFileToHibernateJob customerFile=/input/customer2.csv
 */
@RequiredArgsConstructor
@Configuration
public class CsvFileInsertHibernateJob {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public Job csvFileToHibernateJob() {
        return jobBuilderFactory.get("csvFileToHibernateJob")
                .start(csvFileToHibernateStep())
                .build();
    }

    @Bean
    public Step csvFileToHibernateStep() {
        return stepBuilderFactory.get("csvFileToHibernateStep")
                .<Customer, Customer>chunk(5)
                .reader(customerFlatFileHibernateItemReader(null))
                .writer(hibernateItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerFlatFileHibernateItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerFlatFileItemReader")
                .resource(inputFile)
                .delimited()
                .names(new String[]{"firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode"})
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .build();
    }

    @Bean
    public HibernateItemWriter<Customer> hibernateItemWriter() {

        return new HibernateItemWriterBuilder<Customer>()
                .sessionFactory(entityManagerFactory.unwrap(SessionFactory.class))
                .build();
    }

}
