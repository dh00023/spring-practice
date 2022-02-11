package io.spring.batch.javagradle.book.basic.file.common.skipper;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ParseException;

import java.io.FileNotFoundException;

public class FileVerificationSkipper implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {

        if (t instanceof FileNotFoundException) {
            return false;
        } else if (t instanceof ParseException && skipCount < 10) {
            return true;
        } else {
            return false;
        }
    }
}
