package io.spring.batch.javagradle.book.basic.file.fixed;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * --job.name=fixedWidthFileJob customerFile=/basic/input/customerFixedWidth.txt
 */
@Configuration
@RequiredArgsConstructor
public class FixedWidthFileCopyJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fixedWidthFileJob() {
        return this.jobBuilderFactory.get("fixedWidthFileJob")
                .start(fixedWidthFileStep())
                .build();
    }
    @Bean
    public Step fixedWidthFileStep() {
        return this.stepBuilderFactory.get("fixedWidthFileStep")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader(null))
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}") Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader") // 각 스텝의 ExecutionContext에 추가되는 특정키의 접두문자로 사용될 이름(saveState false인 경우 지정할 필요X)
                .resource(inputFile)
                .fixedLength() // FixedLengthBuilder
                .columns(new Range[]{new Range(1,11), new Range(12,12), new Range(13,22), new Range(23,26)
                        , new Range(27,46), new Range(47,62), new Range(63,64), new Range(65,69)}) // 고정너비
                .names(new String[]{"firstName", "middleInitial", "lastName", "addressNumber", "street"
                        , "city", "state", "zipCode"}) // 각 컬럼명
//                .strict(false) // 정의된 파싱 정보 보다 많은 항목이 레코드에 있는 경우(true 예외)
                .targetType(Customer.class) // BeanWrapperFieldSetMapper 생성해 도메인 클레스에 값을 채움
                .build();
    }

    @Bean
    public ItemWriter<Customer> customerItemWriter() {
        return (items) -> items.forEach(System.out::println);
    }
}
