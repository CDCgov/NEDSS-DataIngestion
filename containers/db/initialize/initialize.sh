#!/usr/bin/env bash
set -e
BASE="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

until /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -Q "select 1" &> /dev/null; do
    sleep 1s
done;

echo "*************************************************************************"
echo "  Initializing NBS databases"
echo "*************************************************************************"

# Enable CLR
echo "*************************************************************************"
echo "  Enabling CLR"
echo "*************************************************************************"
/opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -Q "EXEC sp_configure 'clr enabled', 1; RECONFIGURE;"

for sql in $(find "$BASE/restore.d" -iname "*.sql" | sort) ;
do
    echo "Executing: $sql"
    /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -i "$sql"

    echo "Completed: $sql"
done

echo "*************************************************************************"
echo "  NBS databases ready"
echo "*************************************************************************"


# # Enable CDC at the database level
# echo "*************************************************************************"
# echo "  Enabling CDC at the database level"
# echo "*************************************************************************"
# if ! /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -Q "USE NBS_ODSE; EXEC sp_changedbowner 'sa'; EXEC sys.sp_cdc_enable_db;"; then
#     echo "Error enabling CDC at the database level"
#     exit 1
# fi
# # Enable CDC for each table
# echo "*************************************************************************"
# echo "  Enabling CDC for tables"
# echo "*************************************************************************"
# for table in "Person" "Person_Name" "Person_Race" "Entity_Id" "Tele_Locator" "Postal_Locator"; do
#     echo "Enabling CDC for table: $table"
#     /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -Q "USE NBS_ODSE; EXEC sys.sp_cdc_enable_table @source_schema = 'dbo', @source_name = '$table', @role_name = NULL;"
# done
echo "*************************************************************************"
echo " CDC has been enabled."
echo "*************************************************************************"
