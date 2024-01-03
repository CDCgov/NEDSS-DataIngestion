file_directory="/Users/DucNguyen/Downloads/python_code_for_di/data"

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

# Iterate over each file in the directory
for filename in "$file_directory"/*.txt; do
    if [ -f "$filename" ]; then
        message_content=$(cat "$filename")
        echo "Sending request for file: $filename"
        #echo "Content: $message_content"
        curl -X POST \
            -H "$header1" \
            -H "$header2" \
            -H "$header3" \
            -u "$username:$password" \
            -d "$message_content" \
            "$post_message_url"
    fi
done