INSERT INTO NBS_MSGOUTE.dbo.ecr_constant_lookup (ID,SubjectArea,QuestionIdentifier,QuestionDisplayName,SampleValue,Usage) VALUES
    (1,N'AUTHOR',N'AUT101',N'Author System Local ID',N'2.16.840.1.113883.19.5',N'Sending System OID (Root)'),
    (2,N'AUTHOR',N'AUT102',N'Author System  Name',N'Sara Alert Jurisdiction',N'Sending System Name'),
    (3,N'CUSTODIAN',N'CUS101',N'Custodian Local ID',N'13.3.3.3.333.23',N'Sending Facility OID (Root)'),
    (4,N'CUSTODIAN',N'CUS102',N'Custodian Name',N'STATE DEPT OF HEALTH',N'Sending Facility Name'),
    (5,N'CUSTODIAN',N'CUS103',N'Custodian Street Address 1',N'2 ASDFSADF',N'Sending Facility Address (line 1)'),
    (6,N'CUSTODIAN',N'CUS104',N'Custodian Street Address 2',N'SUITE',N'Sending Facility Address (line 2)'),
    (7,N'CUSTODIAN',N'CUS105',N'Custodian City',N'LINCOLN',N'Sending Facility City'),
    (8,N'CUSTODIAN',N'CUS106',N'Custodian State',N'31^NE^(FIPS 5-2 (State))',N'Sending Facility State'),
    (9,N'CUSTODIAN',N'CUS107',N'Custodian Zip',N'98547',N'Sending Facility Zip'),
    (10,N'CUSTODIAN',N'CUS108',N'Custodian Country',N'840^ UNITED STATES^(Country (ISO 3166))',N'Sending Facility Country');
INSERT INTO NBS_MSGOUTE.dbo.ecr_constant_lookup (ID,SubjectArea,QuestionIdentifier,QuestionDisplayName,SampleValue,Usage) VALUES
    (11,N'CUSTODIAN',N'CUS109',N'Custodian Telephone Work',N'521-895-7854',N'Sending Facility Phone (Use = WP)');
