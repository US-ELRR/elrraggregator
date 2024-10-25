# elrraggregator     
ELRR service which aggregates learner profiles 

Setup elrrdatasync first [README](../elrrdatasync/README.md)

# Dependencies:
- Java JDK 17
- git
- Maven 3
- Docker Desktop

# Tools
- DBeaver
- Eclipse or other IDE

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
 