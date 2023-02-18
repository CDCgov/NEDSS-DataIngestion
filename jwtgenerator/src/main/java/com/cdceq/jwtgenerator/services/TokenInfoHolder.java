package com.cdceq.jwtgenerator.services;

import  lombok.Getter;
import 	lombok.NoArgsConstructor;
import  lombok.Setter;

import  java.util.Date;

@NoArgsConstructor
@Setter
@Getter
public class TokenInfoHolder {
    private String remoteAddress;
    private String passPhrase;
    private String token;
    private Date expiration;
}