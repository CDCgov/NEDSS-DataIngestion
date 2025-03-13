# Liquibase Execution

### Environment Variable set up

The following environment variables assume a local Database instance with NBS_DataIngest database
For any other instances, update the database url and credentials appropriately.

```
export NBS_DBSERVER=localhost
export NBS_DBUSER=sa
export NBS_DBPASSWORD=fake.fake.fake.1234
```

### Liquibase script execution

* Make sure you are in the following project

    * `/NEDSS-DataIngestion/data-ingestion-service`

* Run following to execute liquibase

    * ```./gradlew update```

### Liquibase Error Handling

<b>ValidationFailedException</b>: When change is made to existing change log. This can be handled by removing the conflicting id or revert the changes.
To remove the id, the following command can be run:

```
delete from database.dbo.databasechangelog where id = 'id'
```

