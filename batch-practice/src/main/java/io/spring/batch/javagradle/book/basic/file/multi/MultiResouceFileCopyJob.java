package io.spring.batch.javagradle.book.basic.file.multi;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import io.spring.batch.javagradle.book.basic.file.common.fieldmapper.TransactionFieldSetMapper;
import io.spring.batch.javagradle.book.basic.file.common.reader.MultiResourceCustomerFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
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
 * --job.name=multiResourceFileJob customFile=/basic/input/customerMultiFormat*
 */
@Configuration
@RequiredArgsConstructor
public class MultiResouceFileCopyJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiResourceFileJob() {
        return this.jobBuilderFactory.get("multiResourceFileJob")
                .start(multiResourceFileStep())
                .build();
    }
    @Bean
    public Step multiResourceFileStep() {
        return this.stepBuilderFactory.get("multiResourceFileStep")
                .<Customer, Customer>chunk(10)
                .reader(multiResourceItemReader(null))
                .writer(multiResourceItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader multiResourceItemReader(@Value("#{jobParameters['customFile']}") Resource[] resources) {
        return new MultiResourceItemReaderBuilder<>()
                .name("multiResourceItemReader")
                .resources(resources) // resources ??????
                .delegate(multiResourceCustomerFileReader()) // ?????? ????????? ????????? ?????? ????????????
                .build();
    }

    @Bean
    public MultiResourceCustomerFileReader multiResourceCustomerFileReader() {
        return new MultiResourceCustomerFileReader(multiResourceCustomerItemReader());
    }

    @Bean
    public FlatFileItemReader multiResourceCustomerItemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("multiResourceCustomerItemReader")
                .lineMapper(multiResourceTokenizer())
                .build();
    }

    @Bean
    public PatternMatchingCompositeLineMapper multiResourceTokenizer() {
        Map<String, LineTokenizer> lineTokenizerMap = new HashMap<>(2);

        lineTokenizerMap.put("TRANS*", multiResourceTransactionLineTokenizer()); // TRANS??? ???????????? transactionLineTokenizer
        lineTokenizerMap.put("CUST*", multiResourceCustomerLineTokenizer()); // CUST??? ????????????, customerLineTokenizer

        Map<String, FieldSetMapper> fieldSetMapperMap = new HashMap<>(2);

        BeanWrapperFieldSetMapper<Customer> customerFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        customerFieldSetMapper.setTargetType(Customer.class);

        fieldSetMapperMap.put("TRANS*", new TransactionFieldSetMapper()); // ??????????????? ?????? ?????? ?????? ????????? FieldSetMapper ??????(Date, Double)
        fieldSetMapperMap.put("CUST*", customerFieldSetMapper);

        PatternMatchingCompositeLineMapper lineMappers = new PatternMatchingCompositeLineMapper();

        lineMappers.setTokenizers(lineTokenizerMap);
        lineMappers.setFieldSetMappers(fieldSetMapperMap);

        return lineMappers;
    }

    @Bean
    public DelimitedLineTokenizer multiResourceTransactionLineTokenizer() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        delimitedLineTokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");

        return delimitedLineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer multiResourceCustomerLineTokenizer() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        delimitedLineTokenizer.setNames("firstName", "middleInitial", "lastName", "address", "city", "state", "zipCode");
        delimitedLineTokenizer.setIncludedFields(1,2,3,4,5,6,7); // prefix????????? ?????? ??????

        return delimitedLineTokenizer;
    }


    @Bean
    public ItemWriter multiResourceItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
