package io.roach.stock.config;

import com.zaxxer.hikari.HikariDataSource;
import io.roach.stock.TradingApplication;
import io.roach.stock.annotation.AdvisorOrder;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Configuration for the repository/database layer including transaction management.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true, order = AdvisorOrder.TX_ADVISOR)
@EnableJpaRepositories(basePackageClasses = TradingApplication.class, enableDefaultTransactions = false)
//@EnableJpaAuditing(modifyOnCreate = false, auditorAwareRef = "auditorProvider")
public class JpaConfiguration {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("bobby_tables");
    }

    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = hikariDataSource();
        return ProxyDataSourceBuilder
                .create(new LazyConnectionDataSourceProxy(dataSource))
                .name("SQL-Trace")
                .asJson()
                .multiline()
                .logQueryBySlf4j(SLF4JLogLevel.TRACE, "io.roach.SQL_TRACE")
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource hikariDataSource() {
        HikariDataSource ds = dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setAutoCommit(false);
        ds.addDataSourceProperty("reWriteBatchedInserts", "true");
        ds.addDataSourceProperty("application_name", "Roach Trading");
        return ds;
    }

//    @Bean
//    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }

//    @Bean
//    public PlatformTransactionManager transactionManager(@Autowired EntityManagerFactory emf) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(emf);
//        transactionManager.setJpaDialect(new HibernateJpaDialect());
//        return transactionManager;
//    }
}
