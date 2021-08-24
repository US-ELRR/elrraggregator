# elrraggregator     
ELRR service which aggregates learner profiles 

# Requirements
For building and running the elrraggregator you need:
- JDK 1.8
- Maven 3
# Build the application
- mvn clean install -Dmaven.test.skip=false
# Deploying the application on Docker 
The easiest way to deploy the sample application to Docker is to follow below steps:
- mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
- docker build --build-arg JAR_FILE="./target/elrraggregator-0.0.1-SNAPSHOT.jar" --file Dockerfile -t <docker_hub>/test:elrraggregator-dck-img         
- docker run -p Port:Port -t <docker_hub>/test:elrraggregator-dck-img         

# Running the application locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the main method in the com.deloitte.elrr.elrrconsolidate.ElrrConsolidateApplication class from your IDE
# Alternatively you can use the Spring Boot Maven plugin: 
- mvn spring-boot:run
# Optional step 
- docker push <docker_hub>/test:elrraggregator-dck-img   
