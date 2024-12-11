use NBS_ODSE;
GO

INSERT INTO
	NBS_document_metadata (
		nbs_document_metadata_uid,
		xml_schema_location,
		document_view_xsl,
		description,
		doc_type_cd,
		add_time,
		add_user_id,
		record_status_cd,
		record_status_time,
		xmlbean_factory_class_nm,
		parser_class_nm,
		document_view_cda_xsl,
		DOC_TYPE_VERSION_TXT
	)
VALUES
	(
		1005,
		/* nbs_document_metadata_uid */ 'http://www.cdc.gov/NEDSS  PHDC.xsd',
		/* xml_schema_location */ 'Sample Document View XSL',
		/* document_view_xsl */ 'ELR Document -V(5.4.8/6.0.8)',
		'11648804',
		'2021-07-18T23:13:27.087',
		999999999,
		'Active',
		'2016-06-03T11:43:45.447',
		NULL,
		NULL,
		NULL,
		NULL
);

GO