file_directory="/Users/DucNguyen/Desktop/CDC_Repos/NEDSS-DataIngestion/data-ingestion-service/src/test/load-testing/data"

post_message_url="http://localhost:8081/api/reports"

username="username"
password="password"
header1="msgType: HL7"
header2="validationActive: false"
header3="Content-Type: text/plain"
header4="Content-Length: <calculated when request is sent>"
header5="Host: <calculated when request is sent>"
header6="User-Agent: PostmanRuntime/7.36.0"
header7="Accept: */*"
header8="Accept-Encoding: gzip, deflate, br"
header9="Connection: keep-alive"
header10="Authorization: Basic ZGl0ZWFtYWRtaW46dGVtcDEyMw=="
header11="Cache-Control: no-cache"
header12="Postman-Token: <calculated when request is sent>"
header13="clientid: di-keycloak-client";
header14="clientsecret: eVQ9n5zxyhUTHFmA2XhNfb6r8DYGlSI5";
bearer_token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLN2dmT2k0aW1ucGdmZkN3ZGd3VF9nT2lXYnFoRnQ4c0hqYkJmVHR4TkZnIn0.eyJleHAiOjE3MDY3NjA5OTQsImlhdCI6MTcwNjcyNDk5NCwianRpIjoiNTdiYzJjYWYtOTlhMS00YjZiLTgyZTktYzZhYTE1YTZhYWM3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9OQlMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYjk1OTQzNjMtOTBjOC00NGNlLWIwZjEtYjhiZDFhNzY3MzJhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZGkta2V5Y2xvYWstY2xpZW50IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLW5icyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRIb3N0IjoiMTI3LjAuMC4xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWRpLWtleWNsb2FrLWNsaWVudCIsImNsaWVudEFkZHJlc3MiOiIxMjcuMC4wLjEiLCJjbGllbnRfaWQiOiJkaS1rZXljbG9hay1jbGllbnQifQ.PpFjW1lMRk3sk5y_ZhRNAwdUAQjz5aJCIAbJrUkUvFuCVv9GvuCVcnNELHQ2uA07aArV86-TPPS_TEF_ZB2JqeJInI1T6yartJZGviuG-d9R3h8IV9zFhZ3OOzoHx5TVKkpGXqp_ou6YnjKyCfwT6Jm6FVFdRUzI0XAVwf0eWy0pcTRaLG4PB2vzEHH76CwKlZA8poEHYt2fgJfRGpSnS5ysEV-afEP9zJ5xk8Mazwts8Q3ilO_7plDDlozPNyxtVqX987fKz8ByH9k0r6FrcUQAd3G5Ws2sRMxCMDhJh133A0Z5lhSP87ZENFL_cPNN-g5aEhUjJazYnerV-Oo5iA";
for filename in "$file_directory"/*.txt; do
    if [ -f "$filename" ]; then
        message_content=$(cat "$filename")
        echo "Sending request for file: $filename"
        # Uncomment the next line if you want to print the content
        #echo "Content: $message_content"

        curl -X POST \
            -H "Authorization: Bearer $bearer_token" \
            -H "$header1" \
            -H "$header2" \
            -H "$header3" \
            -H "$header13" \
            -H "$header14" \
            -d "$message_content" \
            "$post_message_url"
    fi
done
