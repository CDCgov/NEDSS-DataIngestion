package com.cdceq.nbsadapter.persistance.model;

import  javax.persistence.Id;

import	lombok.NoArgsConstructor;
import	lombok.Getter;
import	lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EntityHl7Message {
    @Id
    private String id;
    private String timestamp;
    private String source;
    private String data;
}