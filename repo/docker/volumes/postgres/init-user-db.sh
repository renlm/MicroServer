#!/bin/bash
set -e

export PGPASSFILE=/docker-entrypoint-initdb.d/.pgpass
cat <<EOF | tee $PGPASSFILE
localhost:5432:postgres:postgres:$POSTGRES_PASSWORD
EOF

psql -v ON_ERROR_STOP=1 <<-EOSQL
	CREATE USER dev WITH PASSWORD '$POSTGRES_PASSWORD';
	CREATE DATABASE dev OWNER dev;
	GRANT ALL PRIVILEGES ON DATABASE dev TO dev;
EOSQL
