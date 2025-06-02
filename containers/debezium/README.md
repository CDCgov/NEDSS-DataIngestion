# Setting up debezium

## Starting debezium

The docker container is pre-configured to connect to the di-mssql database and kafka broker.

```bash
docker compose up debezium -d
```

## Initializing the connector

The first time the debezium container is started, the connector needs to be initialized. To do this, send the following curl command.

```bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8085/connectors/ -d @containers/debezium/connectors/connector.json
```

Note: If not running the command from the project's base directory, update the `connector.json` file path appropriately.

## Deleting a connector

Use the following command to remove a connector. Replacing `nbs-cdc-test` with the appropriate connector name.

```bash
curl -i -X DELETE -H "Accept:application/json" -H "Content-Type:application/json" "localhost:8085/connectors/nbs-cdc-test"
```

Available connectors can be viewed using the following command

```bash
curl localhost:8085/connectors
```
