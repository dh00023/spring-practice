package io.spring.batch.javagradle.book.basic.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class ParameterValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String fileName = parameters.getString("fileName");

        if (!StringUtils.hasText(fileName)) {
            throw new JobParametersInvalidException("fileName 파라미터가 존재하지 않습니다.");
        } else if (!StringUtils.endsWithIgnoreCase(fileName, ".csv")) {
            throw new JobParametersInvalidException("csv 파일이 아닙니다.");
        }
    }
}
