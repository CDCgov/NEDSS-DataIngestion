FROM mcr.microsoft.com/mssql/server:2022-latest

ARG DATABASE_PASSWORD

ENV ACCEPT_EULA=Y \
  MSSQL_PID='Developer' \
  SQLCMDPASSWORD=${DATABASE_PASSWORD} \
  MSSQL_SA_PASSWORD=${DATABASE_PASSWORD}

USER root

RUN apt-get update \
  && apt-get install mssql-tools18 unixodbc-dev -y && apt-get clean

COPY --chown=mssql ./initialize /var/opt/database/initialize/

USER mssql

# Enable SQL Agent
RUN /opt/mssql/bin/mssql-conf set sqlagent.enabled true

# Start the database and execute initialization scripts so the data becomes part of the image
# This is intended for development only
RUN /opt/mssql/bin/sqlservr & /var/opt/database/initialize/initialize.sh

# remove the backup files to reduce image size
RUN rm /var/opt/database/initialize/restore.d/*.bak

ENTRYPOINT [ "/opt/mssql/bin/sqlservr" ]
