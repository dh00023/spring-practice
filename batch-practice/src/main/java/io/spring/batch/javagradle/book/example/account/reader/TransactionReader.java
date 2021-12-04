package io.spring.batch.javagradle.book.example.account.reader;

import io.spring.batch.javagradle.book.example.account.domain.Transaction;
import lombok.Setter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * 커스텀 ItemReader
 * 레코드에 기록된 수와 실제 읽어들인 레코드 수가 다른 경우 잡을 수행하지 않도록
 * FlatFileItemReader를 래핑하는 ItemReader
 */
public class TransactionReader implements ItemStreamReader<Transaction> {

    @Setter
    private ItemStreamReader<FieldSet> fieldSetReader;
    private int recourdCount = 0;
    private int expectedRecordCount =0;

    // stepExecution을 사용해 Job을 중지 시킬 수 있다.
    private StepExecution stepExecution;

    public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }


    @Override
    public Transaction read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        // Job 실패 테스트를 위해 임의로 오류 발생시키기
        // and the following status: [FAILED]
        // FAILED로 끝난 지점부터 이어서 실행된다.
        // ex) chunk 수가 3이면, 실패한 두번째 청크부터 수행
        if (this.recourdCount == 8) {
            throw new ParseException("This isn't what I hoped to happen");
        }
        return process(fieldSetReader.read());
    }

    public Transaction process(FieldSet fieldSet) {
        Transaction result = null;

        if (fieldSet != null) {
            if (fieldSet.getFieldCount() > 1) {
                result = new Transaction();
                result.setAccountNumber(fieldSet.readString(0));
                result.setTimestamp(fieldSet.readDate(1, "yyyy-MM-DD HH:mm:ss"));
                result.setAmount(fieldSet.readDouble(2));
                System.out.println(result.toString());
                recourdCount++;
            } else {
                expectedRecordCount = fieldSet.readInt(0);

                if (expectedRecordCount != this.recourdCount) {
                    this.stepExecution.setTerminateOnly(); // setTeminateOnly() : 스텝이 완료된 후 배치 종료
                }
            }
        }
        return result;
    }

    /**
     * beforeStep으로 stepExecution을 가져온다.
     * @param stepExecution
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
    /**
     * step 완료시 수행
     *
     * @param stepExecution
     * @return expectedRecordCount(파일 푸터에 있는 수) == recourdCount(읽어들인 레코드 수) : stepExecution.getExitStatus()
     * expectedRecordCount(파일 푸터에 있는 수) != recourdCount(읽어들인 레코드 수) : ExitStatus.STOPPED
     *
     * 트랜지션을 별도로 구성하고 스텝의 ExitStatus를 재정의 해야함.
     */
//    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (recourdCount == expectedRecordCount) {
            return stepExecution.getExitStatus();
        } else {
            return ExitStatus.STOPPED;
        }
    }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        this.fieldSetReader.close();
    }
}
