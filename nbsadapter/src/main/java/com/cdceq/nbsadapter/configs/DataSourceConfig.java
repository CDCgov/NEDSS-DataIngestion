package com.cdceq.nbsadapter.configs;

import  org.springframework.context.annotation.Bean;
import  org.springframework.context.annotation.Configuration;
import 	org.springframework.beans.factory.annotation.Value;
import	org.springframework.context.annotation.DependsOn;

import	org.springframework.boot.jdbc.DataSourceBuilder;

import	com.vault.utils.VaultValuesResolver;

import	javax.sql.DataSource;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

@Configuration
public class DataSourceConfig {
    private static Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    
	@Value("${spring.datasource.driver-class-name}")
	private String vaultDriverClassName;
	
	@Value("${spring.datasource.url}")
	private String vaultDbUrl;
	
	@Value("${spring.datasource.username}")
	private String vaultDbUserName;
	
	@Value("${spring.datasource.password}")
	private String vaultDbUserPassword;

    @Bean("dataSource")
    @DependsOn(value = {
        "vaultValuesResolver"
    })
	public DataSource dataSource() {
		String driverClassName = VaultValuesResolver.getVaultKeyValue(vaultDriverClassName);
		String dbUrl = VaultValuesResolver.getVaultKeyValue(vaultDbUrl);
		String dbUserName = VaultValuesResolver.getVaultKeyValue(vaultDbUserName);
		String dbUserPassword = VaultValuesResolver.getVaultKeyValue(vaultDbUserPassword);

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        
        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbUserPassword);
        
        return dataSourceBuilder.build();
    }
}