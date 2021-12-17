package dh0023.example.springbatchreal.config;

import dh0023.example.springbatchreal.config.db.MysqlMybatisConfig;
import dh0023.example.springbatchreal.config.db.PostgreMybatisConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class TestDataSourceConfig {

    /**
     * https://github.com/spring-projects/spring-framework/issues/15999
     * @return
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("/schema/test.sql")
                .generateUniqueName(true) // Could not shut down embedded database 오류 해결
                .build();
    }


    @Primary
    @Bean(MysqlMybatisConfig.MAIN_MYBATIS_SQL_SEESION_FACTORY)
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/**/mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(PostgreMybatisConfig.P_MYBATIS_SQL_SEESION_FACTORY)
    public SqlSessionFactory pSqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-mysql-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:**/mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }
}
