ALTER PROCEDURE [dbo].[GetEdxPatientMatch_SP]
	@type_cd               VARCHAR(30) = '',
	@match_string          VARCHAR(2000) = '',
	@Patient_uid           BIGINT OUTPUT,
	@match_string_hashcode BIGINT OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT @Patient_uid = MAX(PATIENT_UID)
	FROM EDX_PATIENT_MATCH WITH (NOLOCK)
	WHERE TYPE_CD = @type_cd AND MATCH_STRING = @match_string;
END

