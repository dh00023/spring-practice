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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Slf4j
@Import(PostgreBaseConfig.class)
@Configuration
public class PostgreMybatisConfig {

    public static final String P_MYBATIS_SQL_SEESION_FACTORY = "postgreMybatisSqlSessionFactory";
    public static final String P_MYBATIS_SQL_SEESION_TEMPLATE = "postgreMybatisSqlSessionTemplate";

//    public static final String MYBATIS_READONLY_SEESION_FACTORY = "mainMybatisSqlSessionFactory";

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

    @Bean(P_MYBATIS_SQL_SEESION_FACTORY)
    public SqlSessionFactory sessionFactory(@Qualifier(PostgreBaseConfig.POSTGRE_READER_DATASOURCE) DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-postgre-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return sqlSessionFactoryBean.getObject();
    }


    @Bean(P_MYBATIS_SQL_SEESION_TEMPLATE)
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(P_MYBATIS_SQL_SEESION_FACTORY) SqlSessionFactory sqlsessionFactory) {
        return new SqlSessionTemplate(sqlsessionFactory);
    }
}
