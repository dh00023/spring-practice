package io.spring.batch.javagradle.policy;

import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Random;

public class RandomChunkSizePolicy implements CompletionPolicy {

    private int chunksize;
    private int totalProcessed;
    private Random random = new Random();

    // 청크 완료 여부의 상태를 기반으로 결정 로직 수행
    @Override
    public boolean isComplete(RepeatContext context, RepeatStatus result) {
        if (RepeatStatus.FINISHED == result) {
            return true;
        } else {
            return isComplete(context);
        }
    }

    // 내부 상태를 이용해 청크 완료 여부 판단
    @Override
    public boolean isComplete(RepeatContext context) {
        return this.totalProcessed >= chunksize;
    }

    // 청크의 시작을 알 수 있도록 정책을 초기화
    @Override
    public RepeatContext start(RepeatContext parent) {
        this.chunksize = random.nextInt(10000);
        this.totalProcessed = 0;

        System.out.println("chunk size has been set to => " + this.chunksize);
        return parent;
    }

    // 각 item이 처리가되면 update 메서드가 호출되면서 내부 상태 갱신
    @Override
    public void update(RepeatContext context) {
        this.totalProcessed++;
    }
}