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
        entityManagerFactoryRef = "nbsEntityManagerFactory",
        transactionManagerRef = "nbsTransactionManager",
        basePackages = {
                "gov.cdc.dataprocessing.repository.nbs.msgoute",
        }
)
public class NbsDataSourceConfig {
    @Value("${spring.datasource.nbs.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.nbs.url}")
    private String dbUrl;

    @Value("${spring.datasource.nbs.username}")
    private String dbUserName;

    @Value("${spring.datasource.nbs.password}")
    private String dbUserPassword;

    @Bean(name = "nbsDataSource")
    public DataSource nbsDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbUserPassword);

        return dataSourceBuilder.build();
    }

    @Bean(name = "nbsEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder nbsEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Bean(name = "nbsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean nbsEntityManagerFactory(
            EntityManagerFactoryBuilder nbsEntityManagerFactoryBuilder,
            @Qualifier("nbsDataSource") DataSource nbsDataSource ) {
        return nbsEntityManagerFactoryBuilder
                .dataSource(nbsDataSource)
                .packages("gov.cdc.dataprocessing.repository.nbs.msgoute.model")
                .persistenceUnit("nbs")
                .build();
    }

    @Bean(name = "nbsTransactionManager")
    public PlatformTransactionManager nbsTransactionManager(
            @Qualifier("nbsEntityManagerFactory") EntityManagerFactory nbsEntityManagerFactory ) {
        return new JpaTransactionManager(nbsEntityManagerFactory);
    }
}