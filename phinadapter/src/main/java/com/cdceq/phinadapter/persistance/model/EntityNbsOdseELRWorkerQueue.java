package com.cdceq.phinadapter.persistance.model;

import  javax.persistence.Column;
import  javax.persistence.Entity;
import  javax.persistence.Id;
import  javax.persistence.GeneratedValue;
import	javax.persistence.GenerationType;
import  javax.persistence.Table;

import	java.sql.Timestamp;

import	lombok.NoArgsConstructor;
import	lombok.Getter;
import	lombok.Setter;

@Entity
@Table(name = "ELRWorkerQueue")
@NoArgsConstructor
@Getter
@Setter
public class EntityNbsOdseELRWorkerQueue {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	@Column(name="recordId")
    private Integer recordId;

    @Column(name = "processingStatus", length = 64, nullable = false)
    private String processingStatus;
}