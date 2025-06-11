package gov.cdc.dataprocessing.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
        entityManagerFactoryRef = "srteEntityManagerFactory",
        transactionManagerRef = "srteTransactionManager",
        basePackages = {
                "gov.cdc.dataprocessing.repository.nbs.srte",
        }
)

public class SrteDataSourceConfig {
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.srte.url}")
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

    @Value("${spring.datasource.hikari.pool-name-srte:HIKARI_POOL_NAME_SRTE}")
    private String poolName;

    @Bean(name = "srteDataSource")
    public DataSource srteDataSource() {

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


    @Bean(name = "srteEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder srteEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Bean(name = "srteEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean srteEntityManagerFactory(
            EntityManagerFactoryBuilder srteEntityManagerFactoryBuilder,
            @Qualifier("srteDataSource") DataSource srteDataSource ) {
        return srteEntityManagerFactoryBuilder
                .dataSource(srteDataSource)
                .packages("gov.cdc.dataprocessing.repository.nbs.srte.model")
                .persistenceUnit("srte")
                .build();
    }

    @Bean(name = "srteTransactionManager")
    public PlatformTransactionManager srteTransactionManager(
            @Qualifier("srteEntityManagerFactory") EntityManagerFactory srteEntityManagerFactory ) {
        return new JpaTransactionManager(srteEntityManagerFactory);
    }

    @Bean(name = "srteNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate srteNamedParameterJdbcTemplate(
            @Qualifier("srteDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}