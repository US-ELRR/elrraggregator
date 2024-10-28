# elrraggregator     
ELRR service which aggregates learner profiles 

There are database and kafka dependencies, but there's a repo with a docker-compose that resolves them locally.
[.yaml](https://github.com/US-ELRR/elrrdatasync/tree/dev-pom-update-local-fixes-and-docs-1/dev-resources/docker-compose)

Setup elrrdatasync first [README](https://github.com/US-ELRR/elrrdatasync/blob/dev-pom-update-local-fixes-and-docs-1/README.md)

- Java JDK 1.8 [JDK](https://www.oracle.com/java/technologies/downloads/)
- git [Git](https://git-scm.com/downloads)
- Maven 3 [Maven](https://maven.apache.org/)
- Docker Desktop [Docker](https://www.docker.com/products/docker-desktop/)
- PostgreSQL [PostgreSQL](https://www.postgresql.org/download/)

# Tools
- SQL client or Terminal
- Postman [Postman](https://www.postman.com/downloads/)
- Eclipse or other IDE [Eclipse](https://www.eclipse.org/downloads/packages/)


# Build the application
- mvn clean install

# Deploying the application on Docker 
The easiest way to deploy the sample application to Docker is to follow below steps:
- mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
- docker build --build-arg JAR_FILE="./target/elrraggregator-0.0.1-SNAPSHOT.jar" --file Dockerfile -t <docker_hub>/test:elrraggregator-dck-img         
- docker run -p Port:Port -t <docker_hub>/test:elrraggregator-dck-img         

# Running the application locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the main method in the com.deloitte.elrr.elrrconsolidate.ElrrConsolidateApplication class from your IDE

# Alternatively you can use the Spring Boot Maven plugin: 
- mvn clean
- mvn spring-boot:run -D"spring-boot.run.profiles"=local -e (Windows)
- mvn spring-boot:run -D spring-boot.run.profiles=local -e (Linux)
- Ctrl+C to end --> Terminate batch job = Y

# Optional step 
- docker push <docker_hub>/test:elrraggregator-dck-img 
 