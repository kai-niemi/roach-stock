#!/usr/bin/env bash

# Profiles:
#  verbose
#  crdb-dev
#  crdb-dev-rc
#  crdb-local
#  crdb-local-rc
#  psql-dev
#  psql-dev-rc
#  psql-local
#  psql-local-rc

profile=crdb-dev,verbose
groups=stress

params="\
-D--spring.profiles.active=$profile \
-D--spring.datasource.url=jdbc:postgresql://localhost:26257/stock?sslmode=disable \
-D--spring.datasource.username=root \
-D--spring.datasource.password= \
"

./mvnw -D$params -Dgroups=$groups clean test
