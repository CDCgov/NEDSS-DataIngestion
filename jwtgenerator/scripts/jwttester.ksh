#!/bin/ksh

echo "This script tests jwt generator end points"

echo "Ready to run, go for it? "
read intention

curl http://localhost:8000/jwt/v1/token -H 'APP-PASSPHRASE: a bird in hand is worth two in the bush' -X GET

