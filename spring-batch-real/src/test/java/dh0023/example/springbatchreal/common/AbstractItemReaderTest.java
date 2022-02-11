package dh0023.example.springbatchreal.common;

import org.springframework.batch.item.ItemReader;

public abstract class AbstractItemReaderTest<T> {

    protected ItemReader<T> itemReader;

    protected abstract ItemReader<T> getItemReader() throws Exception;

    public void setUp() throws Exception {
        itemReader = getItemReader();
    }

    protected abstract void emptyInputTest(ItemReader<T> itemReader) throws Exception;
}
