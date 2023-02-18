package com.cdceq.jwtgenerator;

import 	org.springframework.boot.SpringApplication;
import 	org.springframework.boot.autoconfigure.SpringBootApplication;

import	com.vault.utils.VaultValuesResolver;

@SpringBootApplication
public class JwtGenerator {
	public static void main(String[] args) {
	    SpringApplication.run(
	    		new Class[] {
						JwtGenerator.class
	    				, VaultValuesResolver.class
	    		},
	    		args);
	}
}
