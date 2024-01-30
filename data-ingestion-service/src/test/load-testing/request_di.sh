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
bearer_token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLN2dmT2k0aW1ucGdmZkN3ZGd3VF9nT2lXYnFoRnQ4c0hqYkJmVHR4TkZnIn0.eyJleHAiOjE3MDY2ODIwMzYsImlhdCI6MTcwNjY0NjAzNiwianRpIjoiYmM2NTM5OTItNWExMi00YzVjLTk4YzctOTE1OTE3OGIwMzk1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9OQlMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYjk1OTQzNjMtOTBjOC00NGNlLWIwZjEtYjhiZDFhNzY3MzJhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZGkta2V5Y2xvYWstY2xpZW50IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLW5icyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRIb3N0IjoiMTI3LjAuMC4xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWRpLWtleWNsb2FrLWNsaWVudCIsImNsaWVudEFkZHJlc3MiOiIxMjcuMC4wLjEiLCJjbGllbnRfaWQiOiJkaS1rZXljbG9hay1jbGllbnQifQ.2vtJMzdXZvNrwN-1gqWcEjuNe3sEZzQpzu0_ln2QamMB-8M9cIdhkRwM5_HOz5qRbPZ_Zr0EOhpLmMK7m2N8gQ1ZGfIf3mNW53RhGmVPMspGCtZyZoAbli711QA9JqZmZAVTf50pDPyc5xB2IQ_Mc9hlmclKMa28pvT9WJrEiyYztMw3C-fu8YKGt_y00yjfxAzBlP3n7e7aBjcH165HAxfuas2EEJMfkssSnTaL-Cb0Uz3n0xpj-UycokCU5Z85XmHt2lmDQPpX68rUTBy28cJBb0vus-J2Fsa-cXz5foDuAkgHQDHRC_RAOF2NqXX1H1DTyvOvDKtodM5y2y0bXw"
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
