package io.spring.batch.javagradle.example.file.delimited;

import io.spring.batch.javagradle.example.file.common.domain.Customer;
import io.spring.batch.javagradle.example.file.common.fieldmapper.CustomFieldSetMapper;
import io.spring.batch.javagradle.example.file.common.tokenizer.CustomFileLineTokenizer;
import io.spring.batch.javagradle.example.file.common.validator.UniqueLastNameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

/**
 * --job.name=validationDelimitedFileJob customerFile=input/customer.csv
 */
@RequiredArgsConstructor
@Configuration
public class ValidationDelimitedFileCopyJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job validationDelimitedFileJob() {
        return this.jobBuilderFactory.get("validationDelimitedFileJob")
                .start(validationDelimitedFileStep())
                .build();
    }
    @Bean
    public Step validationDelimitedFileStep() {
        return this.stepBuilderFactory.get("validationDelimitedFileStep")
                .<Customer, Customer>chunk(10)
                .reader(validationDelimitedCustomerItemReader(null))
                .processor(validationDelimitedCustomerProcessor()) // processor
                .processor(customerValidatingItemProcessor())
                .writer(validationDelimitedCustomerItemWriter())
                .stream(uniqueLastNameValidator())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> validationDelimitedCustomerItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("validationDelimitedCustomerItemReader") // 각 스텝의 ExecutionContext에 추가되는 특정키의 접두문자로 사용될 이름(saveState false인 경우 지정할 필요X)
                .resource(inputFile)
                .delimited() // default(,) DelimitedLineTokenizer를 사용해 각 레코드를 FieldSet으로 변환
                .names(new String[]{"firstName", "middleInitial", "lastName", "addressNumber", "street"
                        , "city", "state", "zipCode"}) // 각 컬럼명
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
//                .fieldSetMapper(new CustomFieldSetMapper()) // customMapper 설정
                .build();
    }

    /**
     * BeanValidationItemProcessor 설정
     * @return
     */
    @Bean
    public BeanValidatingItemProcessor<Customer> validationDelimitedCustomerProcessor() {
        return new BeanValidatingItemProcessor<>();
    }

    @Bean
    public ValidatingItemProcessor<Customer> customerValidatingItemProcessor() {
        return new ValidatingItemProcessor<>(uniqueLastNameValidator());
    }

    @Bean
    public UniqueLastNameValidator uniqueLastNameValidator() {
        UniqueLastNameValidator uniqueLastNameValidator = new UniqueLastNameValidator();

        uniqueLastNameValidator.setName("uniqueLastNameValidator");

        return uniqueLastNameValidator;
    }

    @Bean
    public ItemWriter<Customer> validationDelimitedCustomerItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
