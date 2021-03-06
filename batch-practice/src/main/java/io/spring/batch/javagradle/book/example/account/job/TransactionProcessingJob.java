package io.spring.batch.javagradle.book.example.account.job;

import io.spring.batch.javagradle.book.example.account.dao.TransactionDao;
import io.spring.batch.javagradle.book.example.account.dao.TransactionDaoSupport;
import io.spring.batch.javagradle.book.example.account.domain.AccountSummary;
import io.spring.batch.javagradle.book.example.account.domain.Transaction;
import io.spring.batch.javagradle.book.example.account.processor.TransactionApplierProcessor;
import io.spring.batch.javagradle.book.example.account.reader.TransactionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class TransactionProcessingJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public TransactionReader transactionReader() {
        return new TransactionReader(fileItemReader(null));
    }

    /**
     * csv????????? ???????????? ???
     * FlatFileItemReader : DB??? ?????? ??????????????? ???????????? ????????? ??? ????????? ????????? ?????????
     * @param inputFile
     * @return
     */
    @Bean
    @StepScope
    public FlatFileItemReader<FieldSet> fileItemReader(
            @Value("#{jobParameters['transactionFile']}") PathResource inputFile) {
        return new FlatFileItemReaderBuilder<FieldSet>()
                .name("fileItemReader")
                .resource(inputFile) // ????????? ???????????? ????????? ????????? ??? ?????? ??????
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PassThroughFieldSetMapper())
                .build();
    }

    /**
     * DB??? ???????????? ???????????? ??????
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(
                        new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .sql("INSERT INTO TRANSACTION " +
                        "(ACCOUNT_SUMMARY_ID, TIMESTAMP, AMOUNT) " +
                        "VALUES ((SELECT ID FROM ACCOUNT_SUMMARY " +
                        "	WHERE ACCOUNT_NUMBER = :accountNumber), " +
                        ":timestamp, :amount)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step importTransactionFileStep() {
        return this.stepBuilderFactory.get("importTransactionFileStep")
//                .startLimit(1) // ????????? limit count?????? ?????? ????????????, ??? ????????? ?????? ->  Maximum start limit exceeded for step: importTransactionFileStepStartMax: 1
                .allowStartIfComplete(true) // ?????? ?????????????????? ????????? ??? ?????? ??????. ?????? ????????? ???????????????, ????????? ????????? ?????? ?????? ????????? ??? ??????
                .<Transaction, Transaction>chunk(3) // chunk ??????
                .reader(transactionReader())
                .writer(transactionWriter(null))
                .allowStartIfComplete(true) // ?????? ??? ????????? ??????
                .listener(transactionReader())
                .build();
    }

    /**
     * AccountSummary ????????? ??????
     * @param dataSource
     * @return
     */
    @Bean
    @StepScope
    public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<AccountSummary>()
                .name("accountSummaryReader")
                .dataSource(dataSource)
                .sql("SELECT ACCOUNT_NUMBER, CURRENT_BALANCE " +
                        "FROM ACCOUNT_SUMMARY A " +
                        "WHERE A.ID IN (" +
                        "	SELECT DISTINCT T.ACCOUNT_SUMMARY_ID " +
                        "	FROM TRANSACTION T) " +
                        "ORDER BY A.ACCOUNT_NUMBER")
                .rowMapper((resultSet, rowNumber) -> {
                    AccountSummary summary = new AccountSummary();

                    summary.setAccountNumber(resultSet.getString("account_number"));
                    summary.setCurrentBalance(resultSet.getDouble("current_balance"));

                    return summary;
                }).build();
    }

    @Bean
    public TransactionDao transactionDao(DataSource dataSource) {
        return new TransactionDaoSupport(dataSource);
    }

    @Bean
    public TransactionApplierProcessor transactionApplierProcessor() {
        return new TransactionApplierProcessor(transactionDao(null));
    }

    /**
     * ????????? ???????????? DB??????
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AccountSummary>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(
                        new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE ACCOUNT_SUMMARY " +
                        "SET CURRENT_BALANCE = :currentBalance " +
                        "WHERE ACCOUNT_NUMBER = :accountNumber")
                .build();
    }

    @Bean
    public Step applyTransactionsStep() {
        return this.stepBuilderFactory.get("applyTransactionsStep")
                .<AccountSummary, AccountSummary>chunk(3)
                .reader(accountSummaryReader(null))
                .processor(transactionApplierProcessor())
                .writer(accountSummaryWriter(null))
                .build();
    }

    /**
     * ??? ????????? ????????????, ?????? ???????????? CSV ?????? ??????
     * @param summaryFile
     * @return
     */
    @Bean
    @StepScope
    public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(
            @Value("#{jobParameters['summaryFile']}") Resource summaryFile) {

        DelimitedLineAggregator<AccountSummary> lineAggregator =
                new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<AccountSummary> fieldExtractor =
                new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"accountNumber", "currentBalance"});
        fieldExtractor.afterPropertiesSet();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<AccountSummary>()
                .name("accountSummaryFileWriter")
                .resource(summaryFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public Step generateAccountSummaryStep() {
        return this.stepBuilderFactory.get("generateAccountSummaryStep")
                .<AccountSummary, AccountSummary>chunk(3)
                .reader(accountSummaryReader(null))
                .writer(accountSummaryFileWriter(null))
                .build();
    }

    @Bean
    public Job transactionJob() {

        // beforeStep?????? stepExecution??? ????????????,
        // etTerminateOnly()??? ?????????????????? ??????????????? ????????? ????????? ?????? ??? ?????? ??? ?????????.
        return this.jobBuilderFactory.get("transactionJob")
//                .preventRestart() // job??? ?????? ????????? ??? ????????? ????????????. -> JobInstance already exists and is not restartable
                .start(importTransactionFileStep())
                .next(applyTransactionsStep())
                .next(generateAccountSummaryStep())
                .build();
//		return this.jobBuilderFactory.get("transactionJob")
//				.start(importTransactionFileStep())
//				.on("STOPPED").stopAndRestart(importTransactionFileStep())
//				.from(importTransactionFileStep()).on("*").to(applyTransactionsStep())
//				.from(applyTransactionsStep()).next(generateAccountSummaryStep())
//				.end()
//				.build();
    }

//    public static void main(String[] args) {
//        List<String> realArgs = new ArrayList<>(Arrays.asList(args));
//
//        realArgs.add("transactionFile=input/transactionFile.csv");
//        realArgs.add("summaryFile=file:///Users/dh0023/Desktop/summaryFile3.csv");
//
//        SpringApplication.run(TransactionProcessingJob.class, realArgs.toArray(new String[realArgs.size()]));
//    }
}