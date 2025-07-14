IF EXISTS (
    SELECT 1
    FROM [nbs_msgoute].[dbo].[MSG_XML_MAPPING]
    WHERE COLUMN_NM = 'pat_name_first_txt' 
      AND DOC_TYPE_CD = '2.16.840.1.113883.10.20.15.2^2016-12-01'
      AND XML_TAG = 'given@!qualifier#1'
)
BEGIN
    UPDATE [nbs_msgoute].[dbo].[MSG_XML_MAPPING]
    SET XML_TAG = 'given@text#1'
    WHERE XML_TAG = 'given@!qualifier#1' 
      AND COLUMN_NM = 'pat_name_first_txt' 
      AND DOC_TYPE_CD = '2.16.840.1.113883.10.20.15.2^2016-12-01';
END