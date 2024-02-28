# Roach Stock

A simplified edition of https://github.com/kai-niemi/roach-trading to simulate an online stock 
trading system using Spring Boot and CockroachDB. This edition supports both PSQL and CRDB (23.2+) 
with the new read-committed isolation level.

# Project Setup

## Prerequisites

- JDK17+ (LTS) (OpenJDK compatible)
- Maven 3.1+ (optional)

## Database Setup

Create the databases:

    cockroach sql --url postgresql://localhost:26257?sslmode=disable -e "CREATE database stock;"

## Building and running from codebase

### Building

The application is built with [Maven 3.1+](https://maven.apache.org/download.cgi). 
Tanuki's Maven wrapper is included (mvnw). All 3rd party dependencies are available 
in public Maven repos.

Clone the project:

    git clone git@github.com:kai-niemi/roach-stock.git

To build and deploy to your local Maven repo, execute:

    cd roach-stock
    ./mvnw clean install

See `run.sh` on how to run different tests.