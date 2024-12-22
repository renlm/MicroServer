#!/bin/bash
set -e

# https://clickhouse.com/docs/en/engines/table-engines
clickhouse client -n <<-EOSQL
    CREATE DATABASE logging;
    CREATE USER logging IDENTIFIED BY '$CLICKHOUSE_PASSWORD';
    GRANT ALL ON logging.* TO logging;
    use dev;
    CREATE NAMED COLLECTION local_mysql_dev AS host = 'mysql', port = 3306, database = 'dev', user = 'dev', password = '$CLICKHOUSE_PASSWORD';
    CREATE NAMED COLLECTION local_postgres_dev AS host = 'postgres', port = 5432, database = 'dev', schema = 'public', user = 'dev', password = '$CLICKHOUSE_PASSWORD';
    CREATE NAMED COLLECTION local_redis_dev AS host = 'redis', port = 6379, db_index = 0, pool_size = 16, password = '$CLICKHOUSE_PASSWORD';
EOSQL
