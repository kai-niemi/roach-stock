package io.roach.stock.config;

import java.util.Optional;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import io.roach.stock.TradingApplication;
import io.roach.stock.annotation.AdvisorOrder;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * Configuration for the repository/database layer including transaction management.
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true, order = AdvisorOrder.TX_ADVISOR)
@EnableJpaRepositories(basePackageClasses = TradingApplication.class, enableDefaultTransactions = false)
@EnableJpaAuditing(modifyOnCreate = false, auditorAwareRef = "auditorProvider")
public class JpaConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("bobby_tables");
    }

    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        private final Formatter basicFormatter = FormatStyle.BASIC.getFormatter();

        private final Formatter highlightFormatter = FormatStyle.HIGHLIGHT.getFormatter();

        @Override
        protected String formatQuery(String query) {
            return highlightFormatter.format(basicFormatter.format(query));
        }
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
        creator.setMultiline(true);

        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener();
        listener.setLogger(logger);
        listener.setLogLevel(SLF4JLogLevel.TRACE);
        listener.setQueryLogEntryCreator(creator);

        HikariDataSource dataSource = hikariDataSource();

        return ProxyDataSourceBuilder
                .create(new LazyConnectionDataSourceProxy(dataSource))
                .name("SQL-Trace")
                .asJson()
                .multiline()
                .listener(listener)
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource hikariDataSource() {
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
