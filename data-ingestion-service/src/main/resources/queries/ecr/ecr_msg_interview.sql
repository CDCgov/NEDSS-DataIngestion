SELECT MSG_CONTAINER_UID,IXS_LOCAL_ID,IXS_INTERVIEWEE_ID
     ,IXS_AUTHOR_ID,IXS_EFFECTIVE_TIME,IXS_INTERVIEW_DT,IXS_INTERVIEW_LOC_CD
     ,IXS_INTERVIEWEE_ROLE_CD,IXS_INTERVIEW_TYPE_CD,IXS_STATUS_CD
FROM MSG_INTERVIEW WHERE MSG_CONTAINER_UID = :MSG_CONTAINER_UID