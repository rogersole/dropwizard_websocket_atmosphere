
# DROPWIZARD + ATMOSPHERE 
## WEB SERVER EXAMPLE

---------

This example aims to serve as an example to integrate `Atmosphere`  to a `Dropwizard` project.
This way, both interfaces, REST and Websocket, can be published from the same project.
It's just for a test purposes, so don't expect any production-ready code in here.

## Index

- [Requirements](#requirements)
- [Development software/frameworks](#development-software/frameworks)
- [Source code and application structure](#source-code-and-application-structure)
- [Configuration/Run steps](#configuration/run-steps)
- [REST endpoints](#rest-endpoints)
- [WEBSOCKET endpoints](#websocket-endpoints)
- [Post-mortem](#post-mortem)

---------

## Requirements

To **compile** the project, it's needed:

- Maven 3.2.5 (if willing to compile/package the project)
- Java 7

To **execute** the project, it's needed:

- Java 7
- PostgreSQL


## Development software/frameworks

Frameworks and packages used for development:

- [Dropwizard 0.8.0](http://www.dropwizard.io/). It contains several Java packages such as:  
-- Jetty for HTTP  
-- Jersey for REST  
-- Jackson for JSON  
-- Guava, Logback, Hibernate, JDBI, Joda Time, etc... 
  
- [Atmosphere 2.3.0-RC6](https://github.com/Atmosphere/atmosphere), for managing Websockets.

- [PostgreSQL 9.4.1](http://postgresapp.com/)


## Source code and application structure

- `example.dropwizard-atmosphere-0.0.1-sources.jar`: jar with all the source code.
- `example.dropwizard-atmosphere-0.0.1.jar`: jar with the application, all in one.
- `config/example.yml`: contains application configurations. Log levels, github credentials, database credentials.
- `create-db.sh`: script that creates a PostgreSQL database and also the table. It inserts into the `nr_user` 
  table the accepted users with their passwords.  
- `run-server.sh`: this script launches the application.


## Configuration/Run steps

- **Building the Jar**. If willing to build the .jar project, it must be done using `package`:

```bash
mvn package
```

- **Update configuration files**  
-- `create-db.sh`: Update script parameters, such as, host, user and database password.  
-- `example.yml`: Update database entry fields with the correct ones, GitHub account (if desired) and log level.

- **Create database, tables and static content**

```bash
# Execute the script, and it will create everything. PostgreSQL must be up and running.
./create-db.sh
```

- **Run the server**

The server can be launched executing the script:

```bash
# Internally it is: "java -jar example.dropwizard-atmosphere-0.0.1.jar server config/example.yml"
./run-server.sh
```

The server is exposed on **localhost**, port **8080**.  
Port 8081 exposes some Dropwizard management resources, such as healthchecks and deadlocks detections.

## REST endpoints

- **Timezone endpoint**

```
GET    http://localhost:8080/rest/timezone?tz=Asia/Tokyo
```

Testing the endpoint and getting a correct response:

```bash
# Call
curl -i -u admin:admin123 -H "Accept: application/json" 
-H "Content-Type: application/json" 
-X GET http://localhost:8080/rest/timezone?tz=Asia/Tokyo

# Response
{
   "timezone":"Asia/Tokyo",
   "time":"2015-04-06T11:09:01.108+09:00"
}
```

Testing the endpoint and getting an error response:

```bash
# Call
curl -i -u admin:admin123 -H "Accept: application/json" 
-H "Content-Type: application/json" 
-X GET http://localhost:8080/rest/timezone?tz=Barcelona

# Response
{
   "error":"com.rogersole.example.dropwizard_atmosphere.exception.TimezoneException",
   "message":"The specified time zone ('Barcelona') is not valid"
}
```

-  **GitHub top active users endpoint**

```
GET    http://localhost:8080/rest/github/topactive?city=Barcelona
```

Testing the endpoint and getting a correct response:

```bash
# Call
curl -i -u admin:admin123 -H "Accept: application/json" 
-H "Content-Type: application/json" 
-X GET http://localhost:8080/rest/github/topactive?city=Barcelona

# Response
[
   {
      "email":"contact@nilportugues.com",
      "name":"Nil Portugués Calderó",
      "repositories":
      [
         "address-format",
         "addressing",
         "AdmingeneratorGeneratorBundle",
         "angular-cordova-workshop",
         "angular-phonecat",
         ...
      ]
   },
   {
      "email":"josep.romero.garcia@gmail.com",
      "name":"joe romero",
      "repositories":	   
      [...]
   },
   ...   
]
```

- **List tuples endpoint**

```
GET    http://localhost:8080/rest/tuple
```

Testing the endpoint and getting a correct response:

```bash
# Call
curl -i -u admin:admin123 -H "Accept: application/json" 
-H "Content-Type: application/json" 
-X GET http://localhost:8080/rest/tuple

# Response
[
   {"email":"roger.sole@gmail.com","repository":"repo4"},
   {"email":"foo@example.com","repository":"foobarbaz"},
   ...
]
```

- **Store tuple endpoint**

```
POST    http://localhost:8080/rest/tuple
```

```bash
# Call
curl -i -u admin:admin123 -H"Accept: application/json" 
-H "Content-Type: application/json" 
-X POST -d "{\"email\":\"test@t.com\", \"repository\":\"test\"}" 
-http://localhost:8080/rest/tuple

# Response
HTTP/1.1 200 OK
```

## WEBSOCKET endpoints

The websocket responses are the same than the endpoints ones.
```
ws://localhost:8080/websocket/timezone
send message: timezone string

ws://localhost:8080/websocket/github/topactive
send message: city string

ws://localhost:8080/websocket/tuple
send message: email/repository JSON
```

---- 

## Post-mortem

### Things to be done
- Unit tests for all the logic
- Unit tests for the authentication (REST and WEBSOCKET ones)
- Unit tests mocking database
- Unit tests mocking interfaces

### Improvements to be done
- Cache on authentication (less access to DB)
- Cache on endpoints results (for repeated input, same result)
- Improve authentication method. Currently using Basic authentication.

### Considerations
- GitHub API v3 has a Rate Limit returned in the Response. A possible improvement is to consider that Rate Limit to not be exceeded and managed accordingly. [https://developer.github.com/v3/rate_limit/](https://developer.github.com/v3/rate_limit/)
- Websockets authentication is only done on the socket opening time, not on every message. For authenticate each message, add an Interceptor to the websocket servlets.