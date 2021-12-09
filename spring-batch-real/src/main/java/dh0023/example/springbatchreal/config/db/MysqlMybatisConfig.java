package dh0023.example.springbatchreal.config.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@Slf4j
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class MysqlMybatisConfig {

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

    @Bean("mysqlMybatisDataSource")
    @ConfigurationProperties(prefix = "databases.mysql.second")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("mysqlMybatisSqlSessionFactory")
    public SqlSessionFactory sessionFactory(@Qualifier("mysqlMybatisDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("mysqlMybatisSqlSessionFactory") SqlSessionFactory sqlsessionFactory) {
        return new SqlSessionTemplate(sqlsessionFactory);
    }
}
