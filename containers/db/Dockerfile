FROM mcr.microsoft.com/mssql/server:2022-latest

ENV ACCEPT_EULA=Y \
  MSSQL_PID='Developer' \
  SQLCMDPASSWORD=${NBS_DBPASSWORD} \
  MSSQL_SA_PASSWORD=${NBS_DBPASSWORD} 

USER root

RUN apt-get update \
  && apt-get install mssql-tools18 unixodbc-dev -y && apt-get clean 

COPY --chown=mssql ./initialize /var/opt/database/initialize/

USER mssql

# Start the database and execute initialization scripts so the data becomes part of the image
# This is intended for development only
RUN /opt/mssql/bin/sqlservr & /var/opt/database/initialize/initialize.sh


ENTRYPOINT [ "/opt/mssql/bin/sqlservr" ]