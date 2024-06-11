package gov.cdc.dataprocessing.config;

import jakarta.persistence.EntityManagerFactory;
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

    @Bean(name = "srteDataSource")
    public DataSource srteDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbUserPassword);

        return dataSourceBuilder.build();
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
}