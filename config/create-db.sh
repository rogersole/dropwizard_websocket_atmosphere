#!/bin/sh
echo "CREATE DATABASE example_dropwizard_atmosphere" | psql -U postgres -p 5432 -h localhost
echo "CREATE TABLE tuple (email VARCHAR(100), repository VARCHAR(255), CONSTRAINT tuple_pk PRIMARY KEY (email, repository))" | psql -U postgres -p 5432 -h localhost example_dropwizard_atmosphere
echo "CREATE TABLE nr_user (id SERIAL NOT NULL, name VARCHAR(255), password VARCHAR(255), CONSTRAINT user_pk PRIMARY KEY (id))" | psql -U postgres -p 5432 -h localhost example_dropwizard_atmosphere
echo "INSERT INTO nr_user(name, password) VALUES('admin','admin123')" | psql -U postgres -p 5432 -h localhost example_dropwizard_atmosphere
echo "CREATED!"