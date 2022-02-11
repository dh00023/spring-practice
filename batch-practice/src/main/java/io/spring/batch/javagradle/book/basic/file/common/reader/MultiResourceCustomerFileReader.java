package io.spring.batch.javagradle.book.basic.file.common.reader;

import io.spring.batch.javagradle.book.basic.file.common.domain.Transaction;
import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.util.ArrayList;

public class MultiResourceCustomerFileReader implements ResourceAwareItemReaderItemStream<Customer> {

    private Object curItem = null;

    private ResourceAwareItemReaderItemStream<Object> delegate;

    public MultiResourceCustomerFileReader(ResourceAwareItemReaderItemStream<Object> delegate) {
        this.delegate = delegate;
    }


    /**
     * Resource를 주입함으로 ItemReader가 파일 관리하는 대신
     * 각 파일을 스프링 배치가 생성해 주입
     * @param resource
     */
    @Override
    public void setResource(Resource resource) {
        System.out.println(resource);
        this.delegate.setResource(resource);
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (curItem == null) {
            curItem = delegate.read(); // 고객 정보를 읽음.
        }

        Customer item = (Customer) curItem;
        curItem = null;

        if (item != null) {
            item.setTransactions(new ArrayList<>());

            // 다음 고객 레코드를 만나기 전까지 거래내역 레코드를 읽는다.
            while (peek() instanceof Transaction) {
                item.getTransactions().add((Transaction) curItem);
                curItem = null;
            }
        }

        return item;
    }

    private Object peek() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
        }
        return curItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
