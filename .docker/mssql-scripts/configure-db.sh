#!/bin/bash

set -euo pipefail

echo "Starting the custom SQL setup..."
sleep 10

# Run the setup script to create the DB and the schema in the DB
echo "Importing custom database"
/opt/mssql-tools18/bin/sqlcmd -S 127.0.0.1 -U sa -P $MSSQL_SA_PASSWORD -C -d master -i /usr/config/setup.sql

echo "Custom SQL setup is done, you now can use the database"