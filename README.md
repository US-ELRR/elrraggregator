# elrrportal     
elrrportal if front application for ELRR enterprise users

# Requirements
For building and running the elrrportal you need:
- nodejs 14.17.0

# Build the application
- npm run build
# Deploying the application on Docker 
The easiest way to deploy the sample application to Docker is to follow below steps:
- docker build --file .Dockerfile -t <docker-hub>/newrepository:elrrportal-dck-img .
- docker run -p port:port  -d -t  <docker-hub>/newrepository:elrrportal-dck-img         

# Running the application locally
- npm run

# Optional step 
- docker push <docker-hub>/newrepository:elrrportal-dck-img         
