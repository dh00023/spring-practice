package dh0023.example.springbatchreal.common;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;

import org.springframework.batch.core.StepExecution;

public abstract class AbstractItemStreamReaderTest<T> extends AbstractItemReaderTest<T> {

    private ItemStream itemStream;

    protected ExecutionContext executionContext = new ExecutionContext();

    protected ItemStream setReaderAsStream() {
        if (itemStream == null) {
            this.itemStream = (ItemStream) itemReader;
        }
        return itemStream;
    }


    protected abstract StepExecution getStepExecution();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setReaderAsStream().open(executionContext);
    }

    public void tearDown() {
        setReaderAsStream().close();
    }
}
