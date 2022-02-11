package io.spring.batch.javagradle.book.example.total.configuration;

import io.spring.batch.javagradle.book.example.total.aggregator.StatementLineAggregator;
import io.spring.batch.javagradle.book.example.total.classifier.NcustomerUpdateClassifier;
import io.spring.batch.javagradle.book.example.total.domain.*;
import io.spring.batch.javagradle.book.example.total.header.StatementHeaderCallback;
import io.spring.batch.javagradle.book.example.total.processor.AccountItemProcessor;
import io.spring.batch.javagradle.book.example.total.validator.NcustomerValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * --job.name=importJob customerFile=example/input/customer_update.csv transactionFile=example/input/transactions.xml outputDirectory=example/output/
 */
@Configuration
@RequiredArgsConstructor
public class ImportJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job importJob() throws Exception {
        return this.jobBuilderFactory.get("importJob")
                .start(importCustomerUpdates())
                .next(importTransactions())
                .next(applyTransactions())
                .next(generateStatements(null))
                .build()
                ;
    }


    @Bean
    public Step importCustomerUpdates() throws Exception {
        return this.stepBuilderFactory.get("importCustomerUpdates")
                .<NcustomerUpdate, NcustomerUpdate>chunk(100)
                .reader(ncustomerUpdateFlatFileItemReader(null))
                .processor(ncustomerUpdateValidatingItemProcessor(null))
                .writer(ncustomerClassifierCompositeItemWriter())
                .build();
    }

    @Bean
    public Step importTransactions() {
        return this.stepBuilderFactory.get("importTransactions")
                .<Ntransaction, Ntransaction>chunk(100)
                .reader(ntransactionStaxEventItemReader(null))
                .writer(ntransactionJdbcBatchItemWriter(null))
                .build();
    }


    @Bean
    public Step applyTransactions() {
        return this.stepBuilderFactory.get("applyTransactions")
                .<Ntransaction, Ntransaction>chunk(100)
                .reader(applyTransactionReader(null))
                .writer(applyTransactionWriter(null))
                .build();
    }


    @Bean
    public Step generateStatements(AccountItemProcessor itemProcessor) {
        return this.stepBuilderFactory.get("generateStatements")
                .<Nstatement, Nstatement>chunk(1)
                .reader(nstatementJdbcCursorItemReader(null))
                .processor(itemProcessor)
                .writer(statementItemWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<NcustomerUpdate> ncustomerUpdateFlatFileItemReader(@Value("#{jobParameters['customerFile']}")Resource inputFile) throws Exception {
        return new FlatFileItemReaderBuilder<NcustomerUpdate>()
                .name("ncustomerUpdateFlatFileItemReader")
                .resource(inputFile)
                .lineTokenizer(customerUpdateLineTokenizer())
                .fieldSetMapper(ncustomerUpdateFieldSetMapper())
                .build();
    }



    /**
     * recordId에 따른 파일 파싱
     * @return
     * @throws Exception
     */
    @Bean
    public LineTokenizer customerUpdateLineTokenizer() throws Exception {

        DelimitedLineTokenizer type1 = new DelimitedLineTokenizer();
        type1.setNames("recordId", "customerId", "firstName", "middleName", "lastName");
        type1.afterPropertiesSet(); // 해당 값 확인

        DelimitedLineTokenizer type2 = new DelimitedLineTokenizer();
        type2.setNames("recordId", "customerId", "address1", "address2", "city", "state", "postalCode");
        type2.afterPropertiesSet();

        DelimitedLineTokenizer type3 = new DelimitedLineTokenizer();
        type3.setNames("recordId", "customerId", "emailAddress", "homePhone", "cellPhone", "workPhone", "notificationPreferences");
        type3.afterPropertiesSet();

        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("1*", type1);
        tokenizers.put("2*", type2);
        tokenizers.put("3*", type3);

        PatternMatchingCompositeLineTokenizer lineTokenizer = new PatternMatchingCompositeLineTokenizer();
        lineTokenizer.setTokenizers(tokenizers);

        return lineTokenizer;
    }

    public FieldSetMapper<NcustomerUpdate> ncustomerUpdateFieldSetMapper() {
        return fieldSet -> {
            switch (fieldSet.readInt("recordId")){
                case 1: return new NcustomerNameUpdate(
                            fieldSet.readLong("customerId"),
                            fieldSet.readString("firstName"),
                            fieldSet.readString("middleName"),
                            fieldSet.readString("lastName")
                        );
                case 2: return new NcustomerAddressUpdate(
                            fieldSet.readLong("customerId"),
                            fieldSet.readString("address1"),
                            fieldSet.readString("address2"),
                            fieldSet.readString("city"),
                            fieldSet.readString("state"),
                            fieldSet.readString("postalCode")
                        );
                case 3:
                    Integer notificationPreference = null;
                    if (StringUtils.hasText(fieldSet.readString("notificationPreferences"))) {
                        notificationPreference = Integer.parseInt(fieldSet.readString("notificationPreferences"));
                    }

                    return new NcustomerContactUpdate(
                            fieldSet.readLong("customerId"),
                            fieldSet.readString("emailAddress"),
                            fieldSet.readString("homePhone"),
                            fieldSet.readString("cellPhone"),
                            fieldSet.readString("workPhone"),
                            notificationPreference
                    );
                default:
                    throw new IllegalArgumentException("부적절한 record type 입니다. : " + fieldSet.readInt("recordId"));
            }
        };
    }

    /**
     * custom validator로 NCUSTOMER테이블에 고객 데이터가 존재하지 않으면 실패
     * @param validator
     * @return
     */
    @Bean
    public ValidatingItemProcessor<NcustomerUpdate> ncustomerUpdateValidatingItemProcessor(NcustomerValidator validator) {
        ValidatingItemProcessor<NcustomerUpdate> validatingItemProcessor = new ValidatingItemProcessor<>(validator);
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public JdbcBatchItemWriter<NcustomerUpdate> ncustomerNameUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<NcustomerUpdate>()
                .dataSource(dataSource)
                .sql("UPDATE NCUSTOMER SET FIRST_NAME = COALESCE(:firstName, FIRST_NAME), " +
                        "MIDDLE_NAME = COALESCE(:middleName, MIDDLE_NAME), " +
                        "LAST_NAME = COALESCE(:lastName, LAST_NAME) " +
                        "WHERE CUSTOMER_ID = :customerId")
                .beanMapped()
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<NcustomerUpdate> ncustomerAddressUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<NcustomerUpdate>()
                .dataSource(dataSource)
                .sql("UPDATE NCUSTOMER SET ADDRESS1 = COALESCE(:address1, ADDRESS1), " +
                        "ADDRESS2 = COALESCE(:address2, ADDRESS2), " +
                        "CITY = COALESCE(:city, CITY), " +
                        "STATE = COALESCE(:state, STATE), " +
                        "POSTAL_CODE = COALESCE(:postalCode, POSTAL_CODE) "  +
                        "WHERE CUSTOMER_ID = :customerId")
                .beanMapped()
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<NcustomerUpdate> ncustomerContactUpdateItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<NcustomerUpdate>()
                .dataSource(dataSource)
                .sql("UPDATE NCUSTOMER SET EMAIL_ADDRESS = COALESCE(:emailAddress, EMAIL_ADDRESS), " +
                        "HOME_PHONE = COALESCE(:homePhone, HOME_PHONE), " +
                        "CELL_PHONE = COALESCE(:cellPhone, CELL_PHONE), " +
                        "WORK_PHONE = COALESCE(:workPhone, WORK_PHONE), " +
                        "NOTIFICATION_PREF = COALESCE(:notificationPreferences, NOTIFICATION_PREF) "  +
                        "WHERE CUSTOMER_ID = :customerId")
                .beanMapped()
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<NcustomerUpdate> ncustomerClassifierCompositeItemWriter() {
        NcustomerUpdateClassifier classifier = new NcustomerUpdateClassifier(
                ncustomerNameUpdateItemWriter(null),
                ncustomerAddressUpdateItemWriter(null),
                ncustomerContactUpdateItemWriter(null)
        );
        ClassifierCompositeItemWriter<NcustomerUpdate> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(classifier);
        return compositeItemWriter;
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Ntransaction> ntransactionStaxEventItemReader(@Value("#{jobParameters['transactionFile']}") Resource transactionFile) {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Ntransaction.class);

        return new StaxEventItemReaderBuilder<Ntransaction>()
                .name("ntransactionStaxEventItemReader")
                .resource(transactionFile)
                .addFragmentRootElements("transaction")
                .unmarshaller(unmarshaller)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Ntransaction> ntransactionJdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Ntransaction>()
                .dataSource(dataSource)
                .sql("INSERT INTO NTRANSACTION(TRANSACTION_ID, ACCOUNT_ACCOUNT_ID, DESCRIPTION, CREDIT, DEBIT, TIMESTAMP)" +
                        "VALUES(:transactionId, :accountId, :description, :credit, :debit, :timestamp)")
                .beanMapped()
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Ntransaction> applyTransactionReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Ntransaction>()
                .name("applyTransactionReader")
                .dataSource(dataSource)
                .sql("SELECT TRANSACTION_ID, ACCOUNT_ACCOUNT_ID, DESCRIPTION, CREDIT, DEBIT, TIMESTAMP " +
                        "FROM NTRANSACTION " +
                        "ORDER BY TIMESTAMP")
                .rowMapper((resultSet, i) -> new Ntransaction(
                        resultSet.getLong("transaction_id"),
                        resultSet.getLong("account_account_id"),
                        resultSet.getString("description"),
                        resultSet.getBigDecimal("credit"),
                        resultSet.getBigDecimal("debit"),
                        resultSet.getTimestamp("timestamp")))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Ntransaction> applyTransactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Ntransaction>()
                .dataSource(dataSource)
                .sql("UPDATE NACCOUNT SET " +
                        "BALANCE = BALANCE + :transactionAmount " +
                        "WHERE ACCOUNT_ID = :accountId")
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Nstatement> nstatementJdbcCursorItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Nstatement>()
                .name("nstatementJdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM NCUSTOMER")
                .rowMapper(((rs, rowNum) -> {
                    Ncustomer customer = new Ncustomer(
                            rs.getLong("customer_id"),
                            rs.getString("first_name"),
                            rs.getString("middle_name"),
                            rs.getString("last_name"),
                            rs.getString("address1"),
                            rs.getString("address2"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("postal_code"),
                            rs.getString("ssn"),
                            rs.getString("email_address"),
                            rs.getString("home_phone"),
                            rs.getString("cell_phone"),
                            rs.getString("work_phone"),
                            rs.getInt("notification_pref")
                    );
                    return new Nstatement(customer);
                }))
                .build();
    }

    @Bean
    public FlatFileItemWriter<Nstatement> individualStatemnetItemWriter() {
        FlatFileItemWriter<Nstatement> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setName("individualStatemnetItemWriter");
        itemWriter.setHeaderCallback(new StatementHeaderCallback());
        itemWriter.setLineAggregator(new StatementLineAggregator());

        return itemWriter;
    }

    @Bean
    @StepScope
    public MultiResourceItemWriter<Nstatement> statementItemWriter(@Value("#{jobParameters['outputDirectory']}") Resource outputDir) {
        return new MultiResourceItemWriterBuilder<Nstatement>()
                .name("statementItemWriter")
                .resource(outputDir)
                .itemCountLimitPerResource(1)
                .delegate(individualStatemnetItemWriter())
                .build();
    }

}
