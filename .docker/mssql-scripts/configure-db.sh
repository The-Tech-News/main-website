#!/bin/bash

set -euo pipefail

DBSTATUS=1
ERRCODE=1
i=0

echo "Starting the custom SQL setup..."

while [[ $DBSTATUS -ne 0 ]] && [[ $i -lt 60 ]] && [[ $ERRCODE -ne 0 ]]; do
	i=$i+1
	DBSTATUS=$(/opt/mssql-tools18/bin/sqlcmd -h -1 -t 1 -S 127.0.0.1 -U sa -P $MSSQL_SA_PASSWORD -C -Q "SET NOCOUNT ON; Select SUM(state) from sys.databases")
	ERRCODE=$?
	echo "Still starting up MSSQL. Counting: $i"
	sleep 1
done

if [ $DBSTATUS -ne 0 ]; then 
	echo "SQL Server took more than 60 seconds to start up or one or more databases are not in an ONLINE state"
	exit 1
fi

# Run the setup script to create the DB and the schema in the DB
echo "Importing custom database"
/opt/mssql-tools18/bin/sqlcmd -S 127.0.0.1 -U sa -P $MSSQL_SA_PASSWORD -C -d master -i /usr/config/setup.sql

echo "Custom SQL setup is done, you now can use the database"