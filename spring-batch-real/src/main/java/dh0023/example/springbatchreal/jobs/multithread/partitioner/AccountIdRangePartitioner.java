package dh0023.example.springbatchreal.jobs.multithread.partitioner;

import dh0023.example.springbatchreal.jobs.mysql.repository.NaccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class AccountIdRangePartitioner implements Partitioner {

    private final NaccountRepository naccountRepository;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * 특정기간 내 존재하는 최소 accountId와 최대 accountId를 가져와 gridSize로 분할처리
     * @param gridSize
     * @return
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        long min = naccountRepository.findMinId(startDate, endDate);
        long max = naccountRepository.findMaxId(startDate, endDate);
        long targetSize = (max - min) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        long number = 0;
        long start = min;
        long end = start + targetSize - 1;

        while (start <= max) {
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);

            if (end >= max) {
                end = max;
            }

            value.putLong("minId", start); // 각 파티션마다 사용될 minId
            value.putLong("maxId", end); // 각 파티션마다 사용될 maxId

            start += targetSize;
            end += targetSize;
            number++;
        }


        return result;
    }
}
