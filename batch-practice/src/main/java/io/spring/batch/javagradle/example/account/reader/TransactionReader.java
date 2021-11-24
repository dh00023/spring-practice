package io.spring.batch.javagradle.example.account.reader;

import io.spring.batch.javagradle.example.account.domain.Transaction;
import lombok.Setter;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
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
    private int recourdCount;
    private int expectedRecordCount;

    public TransactionReader(ItemStreamReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }


    @Override
    public Transaction read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
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
            }
        }
        return result;
    }

    /**
     * step 완료시 수행
     *
     * @param stepExecution
     * @return expectedRecordCount(파일 푸터에 있는 수) == recourdCount(읽어들인 레코드 수) : stepExecution.getExitStatus()
     * expectedRecordCount(파일 푸터에 있는 수) != recourdCount(읽어들인 레코드 수) : ExitStatus.STOPPED
     */
    @AfterStep
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
