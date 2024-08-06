file_directory="/Users/DucNguyen/Downloads/python_code_for_di/data"

post_message_url="localhost:8081/api/elrs"

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
header13="clientid: di-keycloak-client"
header14="clientsecret: eVQ9n5zxyhUTHFmA2XhNfb6r8DYGlSI5"
header15="version: 2"
bearer_token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLN2dmT2k0aW1ucGdmZkN3ZGd3VF9nT2lXYnFoRnQ4c0hqYkJmVHR4TkZnIn0.eyJleHAiOjE3MjMwMTQ4MzYsImlhdCI6MTcyMjk3ODgzNiwianRpIjoiN2U4NmU1NWYtODZiYi00ZDM4LTkwMjQtODExOWY0ZjAzOTFlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9OQlMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYjk1OTQzNjMtOTBjOC00NGNlLWIwZjEtYjhiZDFhNzY3MzJhIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZGkta2V5Y2xvYWstY2xpZW50IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLW5icyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRIb3N0IjoiMTI3LjAuMC4xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWRpLWtleWNsb2FrLWNsaWVudCIsImNsaWVudEFkZHJlc3MiOiIxMjcuMC4wLjEiLCJjbGllbnRfaWQiOiJkaS1rZXljbG9hay1jbGllbnQifQ.aSiPQfUo6jypQ7wwXJFKVmG8sRnhqVEyJ1EHdMWzexKlOGV-ehKHAVSAoiXqA8upP2_DYUDEJJTioRV-CCKZzHD9lb2iExS4GHRQV6-cIJJGKDDJbY_0lG5fiC5N3GZaHSNQdYNYyn8eeFIv1ZwNMmiTqJspQEwPlAQt4HaVm6NPBEez3Nmh9Wj4kTDqGlC2VmO28zqeT-65VcgMdIpwdiDb8SgyJdIFjkx8jZKIofEfXbUcveeBtTUu0KS1jKbHd9efayjPN8Bn24tHpUwQW9YrVDn6jjXCjundPHQh67iY2HQVhmpBaftvVncz_qB8uxnGYcBU6J6GxrkES_972A"

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
            -H "$header15" \
            -d "$message_content" \
            "$post_message_url"
    fi
done
