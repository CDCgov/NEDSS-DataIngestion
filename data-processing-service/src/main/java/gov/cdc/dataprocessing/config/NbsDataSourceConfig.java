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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class NbsDataSourceConfig {
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.nbs.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
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