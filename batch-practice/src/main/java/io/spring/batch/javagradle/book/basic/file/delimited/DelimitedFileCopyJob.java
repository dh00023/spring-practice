package io.spring.batch.javagradle.book.basic.file.delimited;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import io.spring.batch.javagradle.book.basic.file.common.fieldmapper.CustomFieldSetMapper;
import io.spring.batch.javagradle.book.basic.file.common.tokenizer.CustomFileLineTokenizer;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

/**
 * --job.name=delimitedFileJob customerFile=/basic/input/customer.csv
 */
@Configuration
@AllArgsConstructor
public class DelimitedFileCopyJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job delimitedFileJob() {
        return this.jobBuilderFactory.get("delimitedFileJob")
                .start(delimitedFileStep())
                .build();
    }
    @Bean
    public Step delimitedFileStep() {
        return this.stepBuilderFactory.get("delimitedFileStep")
                .<Customer, Customer>chunk(10)
                .reader(delimitedCustomerItemReader(null))
                .writer(delimitedCustomerItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> delimitedCustomerItemReader(@Value("#{jobParameters['customerFile']}") PathResource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("delimitedCustomerItemReader") // ??? ????????? ExecutionContext??? ???????????? ???????????? ??????????????? ????????? ??????(saveState false??? ?????? ????????? ??????X)
                .resource(inputFile)
                .delimited() // default(,) DelimitedLineTokenizer??? ????????? ??? ???????????? FieldSet?????? ??????
                .names(new String[]{"firstName", "middleInitial", "lastName", "addressNumber", "street"
                        , "city", "state", "zipCode"}) // ??? ?????????
//                .targetType(Customer.class) // BeanWrapperFieldSetMapper ????????? ????????? ???????????? ?????? ??????
                .fieldSetMapper(new CustomFieldSetMapper()) // customMapper ??????
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> lineTokenizerCustomerItemReader(@Value("#{jobParameters['customerFile']}") PathResource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("lineTokenizerCustomerItemReader")
                .resource(inputFile)
                .lineTokenizer(new CustomFileLineTokenizer()) // lineTokenzier Custom
                .targetType(Customer.class) // BeanWrapperFieldSetMapper ????????? ????????? ???????????? ?????? ??????
                .build();
    }

    @Bean
    public ItemWriter<Customer> delimitedCustomerItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
