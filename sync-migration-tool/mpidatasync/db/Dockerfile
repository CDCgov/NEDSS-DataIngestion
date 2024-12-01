FROM mcr.microsoft.com/azure-sql-edge:latest
USER root

# Install sqlcmd, see https://docs.microsoft.com/en-us/sql/tools/go-sqlcmd-utility?view=sql-server-ver16
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository "$(wget -qO- https://packages.microsoft.com/config/ubuntu/20.04/prod.list)"
RUN apt-get install -y sqlcmd

USER mssql


# docker-compose environment variables are only visible at runtime
ENV ACCEPT_EULA=1
ENV SQLCMDPASSWORD=P@ssw0rd
ENV SA_PASSWORD=P@ssw0rd



ENTRYPOINT [ "/opt/mssql/bin/sqlservr" ]
