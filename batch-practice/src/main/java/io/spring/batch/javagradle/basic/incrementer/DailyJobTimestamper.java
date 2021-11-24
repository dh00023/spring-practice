package io.spring.batch.javagradle.basic.incrementer;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

public class DailyJobTimestamper implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters parameters) {
        System.out.println(new Date());
        return new JobParametersBuilder(parameters)
                .addDate("executionDate", new Date())
                .toJobParameters();
    }
}
