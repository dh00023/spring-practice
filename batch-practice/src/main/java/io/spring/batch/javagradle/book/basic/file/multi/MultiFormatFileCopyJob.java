package io.spring.batch.javagradle.book.basic.file.multi;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import io.spring.batch.javagradle.book.basic.file.common.fieldmapper.TransactionFieldSetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * --job.name=multiFormatFileJob customFile=/basic/input/customerMultiFormat.csv
 */
@Configuration
@RequiredArgsConstructor
public class MultiFormatFileCopyJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiFormatFileJob() {
        return this.jobBuilderFactory.get("multiFormatFileJob")
                .start(multiFormatFileStep())
                .build();
    }
    @Bean
    public Step multiFormatFileStep() {
        return this.stepBuilderFactory.get("multiFormatFileStep")
                .<Customer, Customer>chunk(10)
                .reader(multiFormatItemReader(null))
                .writer(multiFormatItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader multiFormatItemReader(@Value("#{jobParameters['customFile']}") Resource resource) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("multiFormatItemReader")
                .lineMapper(lineTokenizer())
                .resource(resource)
                .build();
    }

    @Bean
    public PatternMatchingCompositeLineMapper lineTokenizer() {
        Map<String, LineTokenizer> lineTokenizerMap = new HashMap<>(2);

        lineTokenizerMap.put("TRANS*", transactionLineTokenizer()); // TRANS로 시작하면 transactionLineTokenizer
        lineTokenizerMap.put("CUST*", customerLineTokenizer()); // CUST로 시작하면, customerLineTokenizer

        Map<String, FieldSetMapper> fieldSetMapperMap = new HashMap<>(2);

        BeanWrapperFieldSetMapper<Customer> customerFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        customerFieldSetMapper.setTargetType(Customer.class);

        fieldSetMapperMap.put("TRANS*", new TransactionFieldSetMapper()); // 일반적이지 않은 타입 필드 변환시 FieldSetMapper 필요(Date, Double)
        fieldSetMapperMap.put("CUST*", customerFieldSetMapper);

        PatternMatchingCompositeLineMapper lineMappers = new PatternMatchingCompositeLineMapper();

        lineMappers.setTokenizers(lineTokenizerMap);
        lineMappers.setFieldSetMappers(fieldSetMapperMap);

        return lineMappers;
    }

    @Bean
    public DelimitedLineTokenizer transactionLineTokenizer() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        delimitedLineTokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");

        return delimitedLineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer customerLineTokenizer() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        delimitedLineTokenizer.setNames("firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode");
        delimitedLineTokenizer.setIncludedFields(1,2,3,4,5,6,7); // prefix제외한 모든 필드

        return delimitedLineTokenizer;
    }


    @Bean
    public ItemWriter multiFormatItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
