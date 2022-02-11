package io.spring.batch.javagradle.book.basic.file.xml;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import io.spring.batch.javagradle.book.basic.file.common.domain.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * --job.name=xmlFileJob customFile=/basic/input/customer.xml
 */
@Configuration
@RequiredArgsConstructor
public class XmlFileCopyJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

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
