package io.spring.batch.javagradle.example.file.common.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.item.file.FlatFileParseException;

@Slf4j
public class CustomerItemListener {

    @OnReadError
    public void onReadError(Exception e) {
        if (e instanceof FlatFileParseException) {
            FlatFileParseException ffpe = (FlatFileParseException) e;

            StringBuilder sb = new StringBuilder();
            sb.append("오류 발생 라인 : ");
            sb.append(ffpe.getLineNumber());
            sb.append("입력값 : ");
            sb.append(ffpe.getInput());

            log.error(sb.toString(), ffpe);
        } else {
            log.error("오류 발생", e);
        }
    }
}
