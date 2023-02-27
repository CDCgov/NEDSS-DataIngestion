package com.cdceq.nbsadapter;

import 	org.springframework.boot.SpringApplication;
import 	org.springframework.boot.autoconfigure.SpringBootApplication;

import	com.vault.utils.VaultValuesResolver;
import	com.cdceq.nbsadapter.configs.DataSourceConfig;
import	com.cdceq.nbsadapter.configs.MongoDBConfig;

@SpringBootApplication
public class NbsAdapter {
	public static void main(String[] args) {
	    SpringApplication.run(
	    		new Class[] {
	    				  NbsAdapter.class
	    				, VaultValuesResolver.class
	    				, DataSourceConfig.class
						, MongoDBConfig.class
	    		},
	    		args);
	}
}
