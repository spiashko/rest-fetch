package com.spiashko.rfetch.jackson;

import com.spiashko.rfetch.aat.common.AatBasePackageMarker;
import com.spiashko.rfetch.jackson.configs.GeneralConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan(basePackageClasses = AatBasePackageMarker.class)
@EnableJpaRepositories(basePackageClasses = AatBasePackageMarker.class)
@EnableTransactionManagement
@EnableAutoConfiguration
@ActiveProfiles("aat")
@SpringBootTest(classes = {GeneralConfig.class})
@Sql(scripts = {"classpath:sql-test-data/base-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BaseApplicationTest {

}
