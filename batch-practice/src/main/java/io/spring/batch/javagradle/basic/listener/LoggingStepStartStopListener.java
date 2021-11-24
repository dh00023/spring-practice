package io.spring.batch.javagradle.basic.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class LoggingStepStartStopListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " 시작");
    }


    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " 종료");
        return stepExecution.getExitStatus();
//        return ExitStatus.FAILED;
    }
}
