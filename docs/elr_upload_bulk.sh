#!/usr/bin/env bash

usage() {
    echo "Usage: $0 -s <source_dir> -d <dest_dir> -u <url> -t <token> -c <client_secret>"
    echo "  -s : Path to the source folder containing HL7 files"
    echo "  -d : Path to the destination folder for processed files"
    echo "  -u : The API Endpoint URL"
    echo "  -t : Authorization Bearer Token"
    echo "  -c : Client Secret"
    exit 1
}

while getopts "s:d:u:t:c:" opt; do
    case $opt in
        s) SOURCE_DIR="$OPTARG" ;;
        d) DEST_DIR="$OPTARG" ;;
        u) API_URL="$OPTARG" ;;
        t) AUTH_TOKEN="$OPTARG" ;;
        c) CLIENT_SECRET="$OPTARG" ;;
        *) usage ;;
    esac
done

# Check if all required variables are set
if [[ -z "$SOURCE_DIR" || -z "$DEST_DIR" || -z "$API_URL" || -z "$AUTH_TOKEN" || -z "$CLIENT_SECRET" ]]; then
    usage
fi

# Validation checks
if [ ! -d "$SOURCE_DIR" ]; then
    echo "Error: Source directory '$SOURCE_DIR' not found."
    exit 1
fi

if [ ! -d "$DEST_DIR" ]; then
    echo "Creating destination: $DEST_DIR"
    mkdir -p "$DEST_DIR"
fi

# Get file list
files=("$SOURCE_DIR"/*)
total_files=0
for f in "${files[@]}"; do [ -f "$f" ] && ((total_files++)); done

if [ "$total_files" -eq 0 ]; then
    echo "No files found in $SOURCE_DIR"
    exit 0
fi
count=0
echo "Starting upload: $total_files files found."
echo "----------------------------------------------------------"

for file in "${files[@]}"; do
    [ -f "$file" ] || continue

    ((count++))
    filename=$(basename "$file")

    response_file=$(mktemp 2>/dev/null || mktemp -t 'elr')

    http_code=$(curl --request POST \
      --url "$API_URL" \
      --header "authorization: Bearer $AUTH_TOKEN" \
      --header 'clientid: di-keycloak-client' \
      --header "clientsecret: $CLIENT_SECRET" \
      --header 'content-type: text/plain' \
      --header 'msgtype: HL7' \
      --header 'version: ' \
      --data-binary "@$file" \
      --silent \
      --write-out "%{http_code}" \
      --output "$response_file")

    guid=$(cat "$response_file")
    rm "$response_file"

    # Result Handling
    if [ "$http_code" -eq 200 ]; then
        mv "$file" "$DEST_DIR/"
        echo "[$count/$total_files] SUCCESS: $filename"
        echo "   ID: $guid"
    else
        echo "----------------------------------------------------------"
        echo "[$count/$total_files] ERROR: $filename (HTTP $http_code)"
        echo "   Response: $guid"
        echo "Execution halted."
        echo "----------------------------------------------------------"
        exit 1
    fi
done

echo "----------------------------------------------------------"
echo "Done! All $total_files files processed successfully."
