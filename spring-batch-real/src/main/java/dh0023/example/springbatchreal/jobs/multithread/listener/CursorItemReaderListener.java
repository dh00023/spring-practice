package dh0023.example.springbatchreal.jobs.multithread.listener;

import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CursorItemReaderListener implements ItemReadListener<Ncustomer> {

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Ncustomer ncustomer) {
        log.info("Reading customer id={}", ncustomer.getCustomerId());
    }

    @Override
    public void onReadError(Exception ex) {

    }
}
