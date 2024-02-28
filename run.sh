#!/usr/bin/env bash

# Profiles:
#  verbose    - enable verbose SQL logging
#  rc         - enable read-committed isolation (1SR default)
#  crdb-dev   - enable CRDB with preset connection url
#  crdb-local - enable CRDB with local connection url
#  psql-dev   - enable PSQL with preset connection url
#  psql-local - enable PSQL with local connection url

profile=crdb-dev,verbose,rc
groups=stress

params="\
-D--spring.profiles.active=$profile \
"

#params="\
#-D--spring.profiles.active=$profile \
#-D--spring.datasource.url=jdbc:postgresql://localhost:26257/stock?sslmode=disable \
#-D--spring.datasource.username=root \
#-D--spring.datasource.password= \
#"

#params="\
#-D--spring.profiles.active=$profile \
#-D--spring.datasource.url=jdbc:postgresql://192.168.1.99:5432/stock \
#-D--spring.datasource.username=root \
#-D--spring.datasource.password= \
#"

./mvnw $params -Dgroups=$groups clean test
