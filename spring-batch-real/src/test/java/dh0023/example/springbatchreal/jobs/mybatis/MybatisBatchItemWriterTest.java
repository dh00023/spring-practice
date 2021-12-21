package dh0023.example.springbatchreal.jobs.mybatis;

import dh0023.example.springbatchreal.jobs.mybatis.dto.Ncustomer;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IndicativeSentencesGeneration(separator = "> ", generator = DisplayNameGenerator.ReplaceUnderscores.class)
class MybatisBatchItemWriterTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        {
            Configuration configuration = new Configuration();
            Environment environment = new Environment("unittest", new JdbcTransactionFactory(), dataSource);
            configuration.setEnvironment(environment);
            Mockito.when(this.sqlSessionFactory.getConfiguration()).thenReturn(configuration);
            Mockito.when(this.sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(this.sqlSession);
        }
        {
            BatchResult result = new BatchResult(null, null);
            result.setUpdateCounts(new int[] { 1 });
            Mockito.when(this.sqlSession.flushStatements()).thenReturn(Collections.singletonList(result));
        }
    }

    @Test
    void sqlSessionFactory_테스트() throws Exception {

        // @formatter:off
        MyBatisBatchItemWriter<Ncustomer> itemWriter = new MyBatisBatchItemWriterBuilder<Ncustomer>()
                .sqlSessionFactory(this.sqlSessionFactory)
                .statementId("insertNcustomer")
                .build();
        // @formatter:on
        itemWriter.afterPropertiesSet();

        List<Ncustomer> ncustomers = getNcusomers();

        itemWriter.write(ncustomers);

        Mockito.verify(this.sqlSession).update("insertNcustomer", ncustomers.get(0));
        Mockito.verify(this.sqlSession).update("insertNcustomer", ncustomers.get(1));
    }

    @Test
    void sqlSessionTemplate_테스트() throws Exception {

        // @formatter:off
        MyBatisBatchItemWriter<Ncustomer> itemWriter = new MyBatisBatchItemWriterBuilder<Ncustomer>()
                .sqlSessionTemplate(new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH))
                .statementId("insertNcustomer")
                .build();
        // @formatter:on
        itemWriter.afterPropertiesSet();

        List<Ncustomer> ncustomers = getNcusomers();

        itemWriter.write(ncustomers);

        Mockito.verify(this.sqlSession).update("insertNcustomer", ncustomers.get(0));
        Mockito.verify(this.sqlSession).update("insertNcustomer", ncustomers.get(1));
    }


    private List<Ncustomer> getNcusomers() {
        return Arrays.asList(new Ncustomer(1L, "Gibbie Peiro", "131 Killdeer Way",  "28815"),
                            new Ncustomer(2L, "Cathyleen Smaling", "717 Spenser Junction", "85020"));
    }
}