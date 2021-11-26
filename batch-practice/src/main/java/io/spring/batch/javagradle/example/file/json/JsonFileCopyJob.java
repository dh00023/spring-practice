package io.spring.batch.javagradle.example.file.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.batch.javagradle.example.file.common.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;

/**
 * --job.name=jsonFileJob customFile=/input/customer.json
 */
@EnableBatchProcessing
@Configuration
public class JsonFileCopyJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jsonFileJob() {
        return this.jobBuilderFactory.get("jsonFileJob")
                .start(jsonFileStep())
                .build();
    }


    @Bean
    public Step jsonFileStep() {
        return this.stepBuilderFactory.get("jsonFileStep")
                .<Customer, Customer> chunk(10)
                .reader(jsonFileReader(null))
                .writer(jsonItemWriter())
                .build();
    }
    @Bean
    @StepScope
    public JsonItemReader<Customer> jsonFileReader(@Value("#{jobParameters['customFile']}") Resource resource) {

        // Jackson이 JSON을 읽고 쓰는데 사용하는 주요 클래스
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

        JacksonJsonObjectReader<Customer> jsonObjectReader = new JacksonJsonObjectReader<>(Customer.class); // 반환할 클래스 설정
        jsonObjectReader.setMapper(objectMapper); // ObjectMapper

        return new JsonItemReaderBuilder<Customer>()
                .name("jsonFileReader")
                .jsonObjectReader(jsonObjectReader) // 파싱에 사용
                .resource(resource)
                .build();
    }

    @Bean
    public ItemWriter jsonItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }

}
