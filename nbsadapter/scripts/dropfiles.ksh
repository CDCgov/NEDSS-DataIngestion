#!/bin/ksh

SRC_DIR=../hl7-data-files/ELR-ExampleMessages/Elr-ExampleMessages-1.9.2-012216
DKR_DIR=/tmp/hl7files
SLEEP_INTERVAL=10

echo "This script tests hl7 restful and file api's"

for ii in `ls $SRC_DIR`
do
  echo "Ready to copy $ii to $DKR_DIR, go for it? "
  read intention

  echo "Copying $ii to $DKR_DIR"
  cp $SRC_DIR/$ii $DKR_DIR/$ii.hl7

  echo "Ready to send $ii to restful call, go for it? "
  read intention

  curl http://localhost:8080/nbsadapter/v1/hl7 -X POST -d @$SRC_DIR/$ii
done


