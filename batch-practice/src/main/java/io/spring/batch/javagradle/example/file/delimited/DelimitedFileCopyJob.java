package io.spring.batch.javagradle.example.file.delimited;

import io.spring.batch.javagradle.example.file.common.domain.Customer;
import io.spring.batch.javagradle.example.file.common.fieldmapper.CustomFieldSetMapper;
import io.spring.batch.javagradle.example.file.common.tokenizer.CustomFileLineTokenizer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

/**
 * --job.name=delimitedFileJob customerFile=src/main/resources/input/customer.csv
 */
@EnableBatchProcessing
@Configuration
public class DelimitedFileCopyJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

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
                .name("delimitedCustomerItemReader") // 각 스텝의 ExecutionContext에 추가되는 특정키의 접두문자로 사용될 이름(saveState false인 경우 지정할 필요X)
                .resource(inputFile)
                .delimited() // default(,) DelimitedLineTokenizer를 사용해 각 레코드를 FieldSet으로 변환
                .names(new String[]{"firstName", "middleInitial", "lastName", "addressNumber", "street"
                        , "city", "state", "zipCode"}) // 각 컬럼명
//                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .fieldSetMapper(new CustomFieldSetMapper()) // customMapper 설정
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> lineTokenizerCustomerItemReader(@Value("#{jobParameters['customerFile']}") PathResource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("lineTokenizerCustomerItemReader")
                .resource(inputFile)
                .lineTokenizer(new CustomFileLineTokenizer()) // lineTokenzier Custom
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .build();
    }

    @Bean
    public ItemWriter<Customer> delimitedCustomerItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
