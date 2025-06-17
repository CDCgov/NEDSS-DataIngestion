package gov.cdc.dataprocessing.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "odseEntityManagerFactory",
        transactionManagerRef = "odseTransactionManager",
        basePackages = {
                "gov.cdc.dataprocessing.repository.nbs.odse",
        }
)

public class OdseDataSourceConfig {
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;
    @Value("${spring.datasource.odse.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUserName;
    @Value("${spring.datasource.password}")
    private String dbUserPassword;

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

    @Value("${spring.datasource.hikari.pool-name-odse:HIKARI_POOL_DP_ODSE}")
    private String poolName;

    @Bean(name = "odseDataSource")
    public DataSource odseDataSource() {

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

    @Bean(name = "odseEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder odseEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }
    @Bean(name = "odseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean odseEntityManagerFactory(
            EntityManagerFactoryBuilder odseEntityManagerFactoryBuilder,
            @Qualifier("odseDataSource") DataSource odseDataSource ) {
        return odseEntityManagerFactoryBuilder
                .dataSource(odseDataSource)
                .packages("gov.cdc.dataprocessing.repository.nbs.odse.model")
                .persistenceUnit("odse")
                .build();
    }
    @Primary
    @Bean(name = "odseTransactionManager")
    public PlatformTransactionManager odseTransactionManager(
            @Qualifier("odseEntityManagerFactory") EntityManagerFactory odseEntityManagerFactory ) {
        return new JpaTransactionManager(odseEntityManagerFactory);
    }

    @Bean(name = "odseJdbcTemplate")
    public JdbcTemplate odseJdbcTemplate(@Qualifier("odseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "odseNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate odseNamedParameterJdbcTemplate(
            @Qualifier("odseDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}