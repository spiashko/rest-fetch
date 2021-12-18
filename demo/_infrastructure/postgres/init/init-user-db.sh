#!/bin/bash

create_db_user_pass() {
    psql -Upostgres -dpostgres -c "CREATE USER $2 WITH PASSWORD '$3'"
    psql -Upostgres -dpostgres -c "CREATE DATABASE $1 OWNER=$2"
    psql --username "$POSTGRES_USER" --dbname "$1" -c 'CREATE EXTENSION IF NOT EXISTS "uuid-ossp"'
}

create_db_user_pass demo_db  demo_user  demo_pass
