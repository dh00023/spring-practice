package io.spring.batch.javagradle.basic.tasklet;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;


@AllArgsConstructor
public class ExploringTasklet implements Tasklet {

    private JobExplorer jobExplorer;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // 현재 Job이름 조JobExplorer회
        String jobName = chunkContext.getStepContext().getJobName();

        // 이때가지 실행된 모든 JobInstance 조회
        List<JobInstance> instaces = jobExplorer.getJobInstances(jobName, 0, Integer.MAX_VALUE);

        System.out.println(String.format("%d job instances for the job %s", instaces.size(), jobName));

        System.out.println("===========================");

        for (JobInstance instance : instaces) {

            // JobInstance와 관련된 JobExecution
            List<JobExecution> jobExecutions = this.jobExplorer.getJobExecutions(instance);
            System.out.println(String.format("Instance %d had %d executions", instance.getInstanceId(), jobExecutions.size()));

            for (JobExecution jobExecution : jobExecutions) {
                System.out.println(String.format("Execution %d resulted in ExitStatus %s", jobExecution.getId(), jobExecution.getExitStatus()));
            }
        }

        return RepeatStatus.FINISHED;

    }
}
