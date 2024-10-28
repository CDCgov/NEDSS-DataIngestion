package gov.cdc.dataingestion.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
                "gov.cdc.dataingestion.odse.repository",
        }
)
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class OdseDataSourceConfig {
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.odse.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbUserPassword;

    @Bean(name = "odseDataSource")
    public DataSource odseDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbUserPassword);

        return dataSourceBuilder.build();
    }

    @Bean(name = "odseEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder odseEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Bean(name = "odseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean odseEntityManagerFactory(
            EntityManagerFactoryBuilder odseEntityManagerFactoryBuilder,
            @Qualifier("odseDataSource") DataSource odseDataSource) {
        return odseEntityManagerFactoryBuilder
                .dataSource(odseDataSource)
                .packages("gov.cdc.dataingestion.odse.repository")
                .persistenceUnit("odse")
                .build();
    }

    @Primary
    @Bean(name = "odseTransactionManager")
    public PlatformTransactionManager odseTransactionManager(
            @Qualifier("odseEntityManagerFactory") EntityManagerFactory odseEntityManagerFactory) {
        return new JpaTransactionManager(odseEntityManagerFactory);
    }
}
