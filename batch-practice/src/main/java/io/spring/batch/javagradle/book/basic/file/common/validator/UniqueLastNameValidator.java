package io.spring.batch.javagradle.book.basic.file.common.validator;

import io.spring.batch.javagradle.book.basic.file.common.domain.Customer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * JobExecution 간의 상태를 저장하기 위해 ItemStreamSupport 상속
 */
public class UniqueLastNameValidator extends ItemStreamSupport implements Validator<Customer> {

    private Set<String> lastNames = new HashSet<>();

    @Override
    public void validate(Customer value) throws ValidationException {
        if (lastNames.contains(value.getLastName())) {
            throw new ValidationException(value.getLastName() + " lastName이 중복됩니다.");
        }
        this.lastNames.add(value.getLastName());
    }

    @Override
    public void open(ExecutionContext executionContext) {

        String lastNames = getExecutionContextKey("lastNames");

        // lastNames가 Execution에 저장되어있는지 확인 후 저장되어있다면, 스텝 처리 이전에 해당값으로 원복
        if (executionContext.containsKey(lastNames)) {
            this.lastNames = (Set<String>) executionContext.get(lastNames);
        }
    }

    /**
     * 청크 단위로 수행되는데, 오류가 발생할 경우 현재 상태를 ExecutionContext에 저장
     * @param executionContext
     */
    @Override
    public void update(ExecutionContext executionContext) {
        Iterator<String> itr = lastNames.iterator();
        Set<String> copiedLastNames = new HashSet<>();

        while (itr.hasNext()) {
            copiedLastNames.add(itr.next());
        }

        executionContext.put(getExecutionContextKey("lastNames"), copiedLastNames);
    }
}
