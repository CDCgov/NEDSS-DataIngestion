package gov.cdc.dataingestion.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "ingestEntityManagerFactory",
        transactionManagerRef = "ingestTransactionManager",
        basePackages = {
                "gov.cdc.dataingestion.validation.repository",
                "gov.cdc.dataingestion.report.repository",
                "gov.cdc.dataingestion.conversion.repository",
                "gov.cdc.dataingestion.deadletter.repository",
                "gov.cdc.dataingestion.reportstatus.repository"
        }
)
@Configuration
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.driverClassName}")
    private String className;

    @Value("${spring.datasource.dataingest.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;


    @Value("${spring.datasource.hikari.maximum-pool-size:100}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:50}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.idle-timeout:120000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1200000}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout:300000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.pool-name:OdseHikariCP}")
    private String poolName;

    @Bean()
    public DataSource dataSource() {
        String driverClassName = this.className;
        String dbUrl = this.dbUrl;
        String dbUserName = this.userName;
        String dbUserPassword = this.password;


        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setUsername(dbUserName);
        hikariConfig.setPassword(dbUserPassword);

        // HikariCP-specific settings
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setMaxLifetime(maxLifetime);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setPoolName(poolName);

        return new HikariDataSource(hikariConfig);

    }

    @Bean
    public EntityManagerFactoryBuilder ingestEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Bean(name = "ingestEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ingestEntityManagerFactory(
            EntityManagerFactoryBuilder ingestEntityManagerFactoryBuilder,
            @Qualifier("dataSource") DataSource dataSource ) {
        return ingestEntityManagerFactoryBuilder
                .dataSource(dataSource)
                .packages("gov.cdc.dataingestion.validation.repository.model",
                          "gov.cdc.dataingestion.report.repository",
                          "gov.cdc.dataingestion.conversion.repository.model",
                          "gov.cdc.dataingestion.deadletter.repository.model",
                          "gov.cdc.dataingestion.security.model",
                          "gov.cdc.dataingestion.reportstatus.model")
                .persistenceUnit("ingest")
                .build();
    }

    @Bean(name = "ingestTransactionManager")
    public PlatformTransactionManager ingestTransactionManager(
            @Qualifier("ingestEntityManagerFactory") EntityManagerFactory ingestEntityManagerFactory ) {
        return new JpaTransactionManager(ingestEntityManagerFactory);
    }
}
