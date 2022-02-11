package dh0023.example.springbatchreal.common.incremeter;


import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.lang.Nullable;

/**
 * RunIdIncrementer 버그 존재
 * AS-IS : {run.id=1, version=1}로 수행한 Job이 실패한 후
 * TO-BE : {run.id=2}로 수행하더라도 지운 파라미터 포함되어 실행됨
 *          completed with the following parameters: [{run.id=2, version=1}]
 */
public class UniqueRunIdIncrementer extends RunIdIncrementer {
    private static final String RUN_ID = "run.id";

    @Override
    public JobParameters getNext(@Nullable JobParameters parameters) {
        JobParameters params = (parameters == null) ? new JobParameters() : parameters;
        return new JobParametersBuilder()
                .addLong(RUN_ID, params.getLong(RUN_ID, Long.valueOf(0)) + 1)
                .toJobParameters();
    }
}
