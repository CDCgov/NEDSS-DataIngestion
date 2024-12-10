#!/bin/sh
set -e

BASE="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"



CLASSIC_PATH=$BASE/nbs-classic/builder/NEDSSDev
CLASSIC_VERSION=NBS_6.0.16

docker compose up di-mssql -d

echo "Building NBS6 Application"

rm -rf $CLASSIC_PATH && \
  git clone -b $CLASSIC_VERSION git@github.com:cdcent/NEDSSDev.git $CLASSIC_PATH && \
  docker compose -f $BASE/../docker-compose.yml up di-wildfly --build -d && \
  rm -rf $CLASSIC_PATH

echo "**** Classic build complete ****"
echo "http://localhost:7001/nbs/login"
echo ""
echo "**** Available users ****"
echo "*\tmsa"
echo "*\tsuperuser"
echo ""