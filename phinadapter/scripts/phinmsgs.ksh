#!/bin/ksh

echo "This script tests data writes odse db, thus triggering raphsody workflow(s)`"

for ii in `ls *.json`
do
  echo "Ready to send $ii to restful call, go for it? "
  read intention

  curl http://localhost:8090/phinadapter/v1/elrwqactivator -H 'Content-Type: application/json' -X POST -d @$ii
done


