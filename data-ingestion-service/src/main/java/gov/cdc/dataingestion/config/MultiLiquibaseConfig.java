package gov.cdc.dataingestion.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MultiLiquibaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase.dataingest")
    public LiquibaseProperties dataingestLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public DataSource dataingestDataSource(@Qualifier("dataingestLiquibaseProperties") LiquibaseProperties props) {
        return DataSourceBuilder.create()
                .url(props.getUrl())
                .username(props.getUser())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .build();
    }

    @Bean(name = "dataingestLiquibase")
    public SpringLiquibase dataingestLiquibase(
            @Qualifier("dataingestDataSource") DataSource dataSource,
            @Qualifier("dataingestLiquibaseProperties") LiquibaseProperties props) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(props.getChangeLog());
        if (liquibase.getContexts() != null) {
            liquibase.setContexts(String.join(",", liquibase.getContexts()));
        }
        return liquibase;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase.msgoute")
    public LiquibaseProperties msgouteLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public DataSource msgouteDataSource(@Qualifier("msgouteLiquibaseProperties") LiquibaseProperties props) {
        return DataSourceBuilder.create()
                .url(props.getUrl())
                .username(props.getUser())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .build();
    }

    @Bean(name = "msgouteLiquibase")
    public SpringLiquibase msgouteLiquibase(
            @Qualifier("msgouteDataSource") DataSource dataSource,
            @Qualifier("msgouteLiquibaseProperties") LiquibaseProperties props) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(props.getChangeLog());
        if (liquibase.getContexts() != null) {
            liquibase.setContexts(String.join(",", liquibase.getContexts()));
        }
        return liquibase;
    }
}