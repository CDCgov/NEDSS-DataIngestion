package com.cdceq.phinadapter;

import 	org.springframework.boot.SpringApplication;
import 	org.springframework.boot.autoconfigure.SpringBootApplication;

import	com.vault.utils.VaultValuesResolver;
import	com.cdceq.phinadapter.configs.DataSourceConfig;

@SpringBootApplication
public class PhinAdapter {
	public static void main(String[] args) {
	    SpringApplication.run(
	    		new Class[] {
						PhinAdapter.class
	    				, VaultValuesResolver.class
	    				, DataSourceConfig.class
	    		},
	    		args);
	}
}
