# Enterprise Learner Record Repository: ELRRAGGREGATOR
Aggregator service is part of the Enterprise Learner Record Repository Platform. Aggregator is a backend application that decomposes xAPI statements ([xAPI-Spec](https://github.com/adlnet/xAPI-Spec/blob/master/README.md)).  It receives xAPI statements from ELRR-Datasync through [Kafka](https://kafka.apache.org). After parsing the xAPI statements, they are loaded into a DB.

## Prerequisites
### Install Docker & docker-compose
#### Windows & MacOS
- Download and install [Docker Desktop](https://www.docker.com/products/docker-desktop) (docker compose included)


#### Linux
You can download Docker Compose binaries from the
[release page](https://github.com/docker/compose/releases) on this repository.

Rename the relevant binary for your OS to `docker-compose` and copy it to `$HOME/.docker/cli-plugins`

Or copy it into one of these folders to install it system-wide:

* `/usr/local/lib/docker/cli-plugins` OR `/usr/local/libexec/docker/cli-plugins`
* `/usr/lib/docker/cli-plugins` OR `/usr/libexec/docker/cli-plugins`

(might require making the downloaded file executable with `chmod +x`)

### Java
For building and running the Aggregator you need:
- JDK 1.8
- Maven 3

## 1. Clone Repo
Clone this repository locally.

```
git clone https://github.com/US-ELRR/elrraggregator.git
```

## 2. Configure Variables
Some variables are required by the application.  They can be set either through environment variables in the system it will run in or by directly editing the application properties file.

### Environment Variables

- The following environment variables are required:

| Environment Variable      | Description |
| ------------------------- | ----------- |
| PGHOST                    | The hostname or IP of the PostgreSQL instance |
| PGPORT                    | The port to access the PostgreSQL instance on |
| PG_DATABASE               | The database name to use in PostgreSQL |
| PG_RW_USER                | A user with Read and Write permissions on the PostgreSQL Database |
| PG_RW_PASSWORD            | The password for the PG_RW_USER |

### Application Properties

- Edit the [`src/main/resources/application.properties`](src/main/resources/application.properties) and [`src/main/resources/application-local.properties`](src/main/resources/application-local.properties) as necessary
- Properties can be encrypted if desired ([a useful tutorial](https://medium.com/@javatechie/spring-boot-password-encryption-using-jasypt-e92eed7343ab))
- Important properties that can be configured cover PostgreSQL access, Kafka access, and logging levels

## 3. Build

### External Connections

#### PostgreSQL

A PostgreSQL DB must be stood up and accessible.
Tables should be created using the SQL files in ELRR-SERVICES.

#### Kafka

Kafka should be stood up and accessible.
Kafka connection information is defined in the application.properties file.
By default the Kafka connection is anticipated on `elrr-kafka:9092` using the `test-1` topic.

### Build the application
- `mvn clean install -Dmaven.test.skip=false`
### Deploying the application on Docker 
The easiest way to deploy the sample application to Docker is to follow steps below:
- `mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)`
- `docker build --build-arg JAR_FILE="./target/elrraggregator-0.0.1-SNAPSHOT.jar" --file Dockerfile -t <docker_hub>/test:elrraggregator-dck-img`

## 4. Run
### Docker
- `docker run -p Port:Port -t <docker_hub>/test:elrraggregator-dck-img`
### IDE
One way is to execute the main method in the `com.deloitte.elrr.elrrconsolidate.ElrrConsolidateApplication` class from your IDE
### Spring Boot Maven
- `mvn spring-boot:run`
### Optional step 
- `docker push <docker_hub>/test:elrraggregator-dck-img`
