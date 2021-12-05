package io.spring.batch.javagradle.book.example.total.classifier;

import io.spring.batch.javagradle.book.example.total.domain.NcustomerAddressUpdate;
import io.spring.batch.javagradle.book.example.total.domain.NcustomerContactUpdate;
import io.spring.batch.javagradle.book.example.total.domain.NcustomerNameUpdate;
import io.spring.batch.javagradle.book.example.total.domain.NcustomerUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.classify.Classifier;

@RequiredArgsConstructor
public class NcustomerUpdateClassifier implements Classifier<NcustomerUpdate, ItemWriter<? super NcustomerUpdate>> {

    private final JdbcBatchItemWriter<NcustomerUpdate> type1ItemWriter;
    private final JdbcBatchItemWriter<NcustomerUpdate> type2ItemWriter;
    private final JdbcBatchItemWriter<NcustomerUpdate> type3ItemWriter;

    @Override
    public ItemWriter<? super NcustomerUpdate> classify(NcustomerUpdate classifiable) {
        if (classifiable instanceof NcustomerNameUpdate) {
            return type1ItemWriter;
        } else if (classifiable instanceof NcustomerAddressUpdate) {
            return type2ItemWriter;
        } else if (classifiable instanceof NcustomerContactUpdate) {
            return type3ItemWriter;
        } else {
            throw new IllegalArgumentException(classifiable.getClass().getCanonicalName() + "은 불가능한 타입입니다.");
        }
    }
}
