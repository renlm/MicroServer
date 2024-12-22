#!/bin/bash
set -e

export PGPASSFILE=/docker-entrypoint-initdb.d/.pgpass
psql -v ON_ERROR_STOP=1 <<-EOSQL
	CREATE USER share WITH PASSWORD '$POSTGRES_PASSWORD';
	CREATE DATABASE share OWNER share;
	GRANT ALL PRIVILEGES ON DATABASE share TO share;
EOSQL
