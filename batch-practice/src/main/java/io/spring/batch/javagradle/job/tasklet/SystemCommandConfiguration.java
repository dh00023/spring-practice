package io.spring.batch.javagradle.job.tasklet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.ConfigurableSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
@Configuration
public class SystemCommandConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job systemCommandJob() {
        return this.jobBuilderFactory.get("systemCommandJob")
                .start(systemCommandStep())
                .build();
    }

    @Bean
    public Step systemCommandStep() {
        return this.stepBuilderFactory.get("systemCommandStep")
                .tasklet(systemCommandTasklet())
                .build();
    }

    @Bean
    public SystemCommandTasklet systemCommandTasklet() {
        SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();

        // 명령어
        systemCommandTasklet.setCommand("touch tmp.txt");
        systemCommandTasklet.setTimeout(5000);

        // Job이 비정상적으로 종료될 떄 시스템 프로세스와 관련된 스레드를 강제 종료할지 여부
        systemCommandTasklet.setInterruptOnCancel(true);

        // 작업 디렉토리 설정
        systemCommandTasklet.setWorkingDirectory("/Users/dh0023/Develop/gitbook/TIL");

        // 시스템 반환 코드를 스프링 배치 상태 값으로 매핑
        systemCommandTasklet.setSystemProcessExitCodeMapper(touchCodeMapper());

        // 비동기로 실행하는 시스템 명령을 주기적으로 완료 여부 확인, 완료 여부를 확인하는 주기(default=1초)
        systemCommandTasklet.setTerminationCheckInterval(5000);

        // 시스템 명령을 실행하는 TaskExecutor 구성 가능
        // 문제가 발생하면 락이 발생할 수 있으므로, 동기방식으로 구현하지 않는 것이 좋다.
        systemCommandTasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());

        // 명령을 실행하기 전에 설정하는 환경 파라미터 목록
        systemCommandTasklet.setEnvironmentParams(
                new String[]{"BATCH_HOME=/Users/dh0023/Develop/spring/spring-practice/batch-practice"});

        return systemCommandTasklet;
    }

    @Bean
    public SimpleSystemProcessExitCodeMapper touchCodeMapper() {
        // 반환된 시스템 코드가 0이 ExitStatus.FINISHED
        // 0이 아니면 ExitStatus.FAILED
        return new SimpleSystemProcessExitCodeMapper();
    }

    @Bean
    public ConfigurableSystemProcessExitCodeMapper configurableSystemProcessExitCodeMapper() {
        // 일반적인 구성 방법으로 매핑 구성을 할 수 있음.
        ConfigurableSystemProcessExitCodeMapper mapper = new ConfigurableSystemProcessExitCodeMapper();

        Map<Object, ExitStatus> mappings = new HashMap<Object, ExitStatus>() {
            {
                put(0, ExitStatus.COMPLETED);
                put(1, ExitStatus.FAILED);
                put(2, ExitStatus.EXECUTING);
                put(3, ExitStatus.NOOP);
                put(4, ExitStatus.UNKNOWN);
                put(ConfigurableSystemProcessExitCodeMapper.ELSE_KEY, ExitStatus.UNKNOWN);
            }
        };

        mapper.setMappings(mappings);

        return mapper;
    }
}
