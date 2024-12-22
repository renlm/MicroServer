#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    CREATE DATABASE logging;
    CREATE USER IF NOT EXISTS 'logging' IDENTIFIED BY 'KK_fyw4bnmdf4Ksp7IMYuPWD@20stdt^tn50jlsfy';
    GRANT ALL ON logging.* TO 'logging';
    use logging;
    CREATE NAMED COLLECTION local_mysql_dev AS
        host = 'mysql',
        port = 3306,
        database = 'dev',
        user = 'dev',
        password = 'KK_fyw4bnmdf4Ksp7IMYuPWD@20stdt^tn50jlsfy';
EOSQL
