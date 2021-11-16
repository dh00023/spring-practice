package io.spring.batch.javagradle;

import io.spring.batch.javagradle.incrementer.DailyJobTimestamper;
import io.spring.batch.javagradle.validator.JobLoggerListener;
import io.spring.batch.javagradle.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@EnableBatchProcessing // 배치 작업에 필요한 빈을 미리 등록하여 사용, 애플리케이션 내 한번만 적용하면 됨.
@SpringBootApplication
public class JavaGradleApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 실제 스프링 배치 Job 생성
     */
    @Bean
    public Job job() {
        // jobBuilderFactory.get("잡이름")
        return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .validator(validator())
                .incrementer(new DailyJobTimestamper())
                .listener(new JobLoggerListener())
//                .incrementer(new RunIdIncrementer())
                .next(step2())
                .build(); // 실제 job 생성
    }

    /**
     * 실제 스프링 배치 step 생성
     * @return
     */
    @Bean
    public Step step1() {
        // stepBuilderFactory.get("스탭 이름")
        // tasklet 구현체
        return this.stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Hello, world!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean Step step2(){
        return this.stepBuilderFactory.get("step2")
//                .tasklet(chunkContextParamsTasklet())
                .tasklet(lateBindingParamTasklet("test"))
                .build();
    }

    @Bean
    public Tasklet chunkContextParamsTasklet() {
        return ((contribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                                .getJobParameters()
                                .get("name");

            System.out.println(String.format("Hello, %s", name));
            return RepeatStatus.FINISHED;
        });
    }

    @StepScope
    @Bean
    public Tasklet lateBindingParamTasklet(@Value("#{jobParameters['name']}") String name) {
        return ((contribution, chunkContext) -> {
            System.out.println(String.format("Hello, %s", name));
            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    public CompositeJobParametersValidator validator() {

        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        // 파라미터 유무 검증
        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();

        defaultJobParametersValidator.setRequiredKeys(new String[] {"fileName"}); // 필수 파라미터 확인
        defaultJobParametersValidator.setOptionalKeys(new String[] {"name", "run.id", "executionDate"}); // 선택 파라미터
        defaultJobParametersValidator.afterPropertiesSet(); // 선택 파라미터에 필수 파라미터가 포함되지 않았는지 확인

        validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));
        return validator;
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaGradleApplication.class, args);
    }
}
