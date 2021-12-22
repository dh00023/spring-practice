package dh0023.example.springbatchreal.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan("dh0023.example.springbatchreal.jobs.mysql.entity")
@EnableJpaRepositories("dh0023.example.springbatchreal.jobs.mysql.repository")
@EnableTransactionManagement
public class TestJpaConfig {

}
