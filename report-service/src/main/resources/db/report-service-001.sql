
-- changeset liquibase:1
--
-- Creates the tables used by Data Ingestion Service
--
CREATE TABLE elr_raw (
	id uniqueidentifier DEFAULT newid() NOT NULL,
	message_type nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	payload ntext COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	updated_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_on datetime DEFAULT getdate() NOT NULL,
	updated_on datetime NULL,
	CONSTRAINT PK__elr_raw__3213E83F108B8188 PRIMARY KEY (id)
);


CREATE TABLE elr_validated (
	id uniqueidentifier DEFAULT newid() NOT NULL,
	raw_message_id uniqueidentifier NULL,
	message_type nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	message_version nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	validated_message ntext COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	updated_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_on datetime DEFAULT getdate() NOT NULL,
	updated_on datetime NULL,
	hashed_hl7_string varchar(64) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CONSTRAINT PK__elr_vali__3213E83FBDBA6B93 PRIMARY KEY (id)
);

ALTER TABLE elr_validated ADD CONSTRAINT FK__elr_valid__raw_i__5FB337D6 FOREIGN KEY (raw_message_id) REFERENCES elr_raw(id);


CREATE TABLE elr_fhir (
	id uniqueidentifier DEFAULT newid() NOT NULL,
	fhir_message nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	raw_message_id uniqueidentifier NULL,
	created_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	updated_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_on datetime DEFAULT getdate() NOT NULL,
	updated_on datetime NULL,
	CONSTRAINT PK__elr_fhir__3213E83FC8183E1D PRIMARY KEY (id)
);

ALTER TABLE elr_fhir ADD CONSTRAINT FK__elr_fhir__raw_me__5AEE82B9 FOREIGN KEY (raw_message_id) REFERENCES elr_raw(id);


CREATE TABLE elr_dlt (
	error_message_id uniqueidentifier NOT NULL,
	error_message_source nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	error_stack_trace nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	error_stack_trace_short nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	dlt_status nvarchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	dlt_occurrence int NULL,
	message ntext COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	updated_by nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_on datetime DEFAULT getdate() NOT NULL,
	updated_on datetime NULL,
	CONSTRAINT PK__elr_dlt__CCC56D623E5B71D2 PRIMARY KEY (error_message_id)
);


CREATE TABLE clients (
	id uniqueidentifier DEFAULT newid() NOT NULL,
	client_id varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	client_secret varchar(512) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	client_roles varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	created_by varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	updated_by varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_on datetime DEFAULT getdate() NOT NULL,
	updated_on datetime NULL,
	CONSTRAINT PK__clients__3213E83F1D101DC2 PRIMARY KEY (id),
	CONSTRAINT UQ__clients__BF21A425ECA9C5BB UNIQUE (client_id)
);
