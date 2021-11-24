package io.spring.batch.javagradle.basic.configurer;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public class CustomBatchConfigurer extends DefaultBatchConfigurer {

    @Autowired
    @Qualifier("repositoryDataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("batchTransactionManager")
    private PlatformTransactionManager transactionManager;

    @Override
    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDatabaseType(DatabaseType.MYSQL.getProductName());
        factory.setTablePrefix("FOO_"); // 테이블명이 BATCH_에서 FOO_로 구성
        factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ"); // 데이터 생성시 트랜잭션 격리 수준
        factory.setDataSource(this.dataSource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    protected JobExplorer createJobExplorer() throws Exception {
        JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();

        jobExplorerFactoryBean.setDataSource(this.dataSource);
        jobExplorerFactoryBean.setTablePrefix("FOO_");

        jobExplorerFactoryBean.afterPropertiesSet();
        return jobExplorerFactoryBean.getObject();
    }
}
