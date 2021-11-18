package io.spring.batch.javagradle.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloWorld implements Tasklet {
    public static final String HELLO_WORLD = "Hello, %s";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String name = (String) chunkContext.getStepContext()
                        .getJobParameters()
                        .get("name");

        // 1. Job의 ExecutionContext를 가져오기
//        ExecutionContext jobContext = chunkContext.getStepContext()
//                                            .getStepExecution()
//                                            .getJobExecution()
//                                            .getExecutionContext();

        // 2. Step ExecutionContext
        ExecutionContext stepContext = chunkContext.getStepContext()
                                            .getStepExecution()
                                            .getExecutionContext();


        stepContext.put("user.name", name);

        System.out.println(String.format(HELLO_WORLD, name));

        return RepeatStatus.FINISHED;
    }
}
