package dh0023.example.springbatchreal.config;

import dh0023.example.springbatchreal.config.db.MysqlJpaConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan(MysqlJpaConfig.PACKAGE)
@EnableJpaRepositories(MysqlJpaConfig.PACKAGE)
@EnableTransactionManagement
public class TestJpaConfig {

}
