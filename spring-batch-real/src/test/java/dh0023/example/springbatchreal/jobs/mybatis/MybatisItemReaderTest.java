package dh0023.example.springbatchreal.jobs.mybatis;

import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class MybatisItemReaderTest {
    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private Cursor<Object> cursor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCloseOnFailing() throws Exception {

        Mockito.when(this.sqlSessionFactory.openSession(ExecutorType.SIMPLE)).thenReturn(this.sqlSession);
        Mockito.when(this.cursor.iterator()).thenReturn(getNcusomers().iterator());
        Mockito.when(this.sqlSession.selectCursor("getNcustomer", Collections.singletonMap("customerId", 3)))
                .thenThrow(new RuntimeException("error."));

        MyBatisCursorItemReader<Ncustomer> itemReader = new MyBatisCursorItemReader<>();
        itemReader.setSqlSessionFactory(this.sqlSessionFactory);
        itemReader.setQueryId("getNcustomer");
        itemReader.setParameterValues(Collections.singletonMap("customerId", 3));
        itemReader.afterPropertiesSet();

        ExecutionContext executionContext = new ExecutionContext();
        try {
            itemReader.open(executionContext);
            fail();
        } catch (ItemStreamException e) {
            Assertions.assertThat(e).hasMessage("Failed to initialize the reader").hasCause(new RuntimeException("error."));
        } finally {
            itemReader.close();
            Mockito.verify(this.sqlSession).close();
        }

    }

    @Test
    public void MybatisCursorItemReaderBuilderTest() throws Exception {
        Mockito.when(this.sqlSessionFactory.openSession(ExecutorType.SIMPLE)).thenReturn(this.sqlSession);
        Mockito.when(this.cursor.iterator()).thenReturn(getNcusomers().iterator());
        Mockito.when(this.sqlSession.selectCursor("getNcustomer", Collections.singletonMap("customerId", 1))).thenReturn(this.cursor);


        // @formatter:off
        MyBatisCursorItemReader<Ncustomer> itemReader = new MyBatisCursorItemReaderBuilder<Ncustomer>()
                .sqlSessionFactory(this.sqlSessionFactory)
                .queryId("getNcustomer")
                .parameterValues(Collections.singletonMap("customerId", 1))
                .build();
        // @formatter:on
        itemReader.afterPropertiesSet();

        ExecutionContext executionContext = new ExecutionContext();
        itemReader.open(executionContext);

        Assertions.assertThat(itemReader.read()).extracting(Ncustomer::getCustomerId).isEqualTo(1L);
        Assertions.assertThat(itemReader.read()).extracting(Ncustomer::getFullName).isEqualTo("Cathyleen Smaling");

        itemReader.update(executionContext);
        Assertions.assertThat(executionContext.getInt("MyBatisCursorItemReader.read.count")).isEqualTo(2);
        Assertions.assertThat(executionContext.containsKey("MyBatisCursorItemReader.read.count.max")).isFalse();

        Assertions.assertThat(itemReader.read()).isNull();
    }

    private List<Object> getNcusomers() {
        return Arrays.asList(new Ncustomer(1L, "Gibbie Peiro", "131 Killdeer Way",  "28815"),
                            new Ncustomer(2L, "Cathyleen Smaling", "717 Spenser Junction", "85020"));
    }
}