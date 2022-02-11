package dh0023.example.springbatchreal.config.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Slf4j
@Import(MysqlBaseConfig.class)
@Configuration
public class MysqlMybatisConfig {

    public static final String MAIN_MYBATIS_SQL_SEESION_FACTORY = "mainMybatisSqlSessionFactory";
    public static final String MAIN_SQL_SESSION_TEMPLATE = "mainSqlSessionTemplate";

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

    @Primary
    @Bean(MAIN_MYBATIS_SQL_SEESION_FACTORY)
    public SqlSessionFactory sessionFactory(@Qualifier(MysqlBaseConfig.MAIN_READER_DATASOURCE) DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(MAIN_SQL_SESSION_TEMPLATE)
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(MAIN_MYBATIS_SQL_SEESION_FACTORY) SqlSessionFactory sqlsessionFactory) {
        return new SqlSessionTemplate(sqlsessionFactory);
    }
}
