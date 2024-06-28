package gov.cdc.dataingestion.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import  org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import  org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import  org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import  org.springframework.transaction.PlatformTransactionManager;
import  org.springframework.orm.jpa.JpaTransactionManager;
import  org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import  javax.sql.DataSource;
import  jakarta.persistence.EntityManagerFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import  java.util.HashMap;

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

    @Bean()
    public DataSource dataSource() {
        String driverClassName = this.className;
        String dbUrl = this.dbUrl;
        String dbUserName = this.userName;
        String dbUserPassword = this.password;

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbUserPassword);

        return dataSourceBuilder.build();
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
