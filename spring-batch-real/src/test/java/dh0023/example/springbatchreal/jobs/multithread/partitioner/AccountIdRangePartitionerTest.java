package dh0023.example.springbatchreal.jobs.multithread.partitioner;

import dh0023.example.springbatchreal.jobs.mysql.repository.NaccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith({MockitoExtension.class})
class AccountIdRangePartitionerTest {

    private static AccountIdRangePartitioner partitioner;

    @Mock
    private NaccountRepository naccountRepository;

    @Test
    void GRID_SIZE_분할_테스트() {

        // given
        // Mockito 라이브러리를 사용해 특정 메서드 호출시 지정된 값이 반환되도록 설정
        Mockito.lenient()
                .when(naccountRepository.findMinId(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(1L);

        Mockito.lenient()
                .when(naccountRepository.findMaxId(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(100L);

        partitioner = new AccountIdRangePartitioner(naccountRepository, LocalDate.of(2018, 5,1), LocalDate.of(2018, 5, 31));

        // when
        Map<String, ExecutionContext> executionContextMap = partitioner.partition(5);

        // then
        ExecutionContext partition1 = executionContextMap.get("partition0");
        assertThat(partition1.getLong("minId")).isEqualTo(1L);
        assertThat(partition1.getLong("maxId")).isEqualTo(20L);

    }

}