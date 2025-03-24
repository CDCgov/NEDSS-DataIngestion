FROM ghcr.io/cdcgov/recordlinker:v25.3.0


# Install drivers for mssql database connection
RUN apt-get install -y gnupg2 apt-transport-https && \
    curl https://packages.microsoft.com/keys/microsoft.asc | \
    curl https://packages.microsoft.com/config/debian/11/prod.list | tee /etc/apt/sources.list.d/mssql-release.list && \
    apt-get update && \
    ACCEPT_EULA=Y apt-get install -y msodbcsql18 unixodbc-dev \
    && apt-get clean
