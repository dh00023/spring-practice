package io.spring.batch.javagradle.example.file.xml;

import io.spring.batch.javagradle.example.file.common.domain.Customer;
import io.spring.batch.javagradle.example.file.common.domain.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * --job.name=xmlFileJob customFile=/input/customer.xml
 */
@EnableBatchProcessing
@Configuration
public class XmlFileCopyJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job xmlFileJob() {
        return this.jobBuilderFactory.get("xmlFileJob")
                .start(xmlFileStep())
                .build();
    }


    @Bean
    public Step xmlFileStep() {
        return this.stepBuilderFactory.get("xmlFileStep")
                .<Customer, Customer> chunk(10)
                .reader(staxCustomerFileReader(null))
                .writer(xmlItemWriter())
                .build();
    }
    @Bean
    @StepScope
    public StaxEventItemReader<Customer> staxCustomerFileReader(@Value("#{jobParameters['customFile']}")Resource resource) {
        return new StaxEventItemReaderBuilder<Customer>()
                .name("staxCustomerFileReader")
                .resource(resource)
                .addFragmentRootElements("customer") // 프레그먼트 루트 엘리먼트
                .unmarshaller(customerMarshaller()) // XML을 도메인 객체로 반환 JAXB 사용
                .build();
    }

    @Bean
    public Jaxb2Marshaller customerMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Customer.class, Transaction.class);

        return jaxb2Marshaller;
    }

    @Bean
    public ItemWriter xmlItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

}
