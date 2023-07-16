# TFTFieldAnalysis
Setting the combinatoric nature of TFT up as a vector model for further processing. 

## Stack

### Backend

Java (17) Spring Boot following standard Model, View, Controller, Service Spring Boot setup. This is a standalone web service that does not include any type of templating or Views, however.
Packages of note:
1. "gbw.riot.tftfieldanalysis.controllers": All controllers handling the api methods exposed.
2. "gbw.riot.tftfieldanalysis.services": All services doing the majority of the processing.
3. "gbw.riot.tftfieldanalysis.core": Contains most type declarations alongside the declarations for DataModel and such.
4. "gbw.riot.tftfieldanalysis.responseUtil.dtos": Contains the data transfer type declarations alongside the functionality required to convert from internal type to external.

Further API documentation can be accessed when running through open-api at "localhost:13498/v3/api-docs". The corresponding swagger ui can be found at "localhost:13498/swagger-ui/index.html"

#### Local Deployment

The project can be run through your IDE of choice or through a cli using "java -jar" targeting the artefact at INSERT LOCATION HERE.
The project does rely on a Postgres container, which requires docker to be running before it can start. The container is not used right now (will for sure be in the future) and I don't know how to turn it off.

### Frontend 

"Standalone" web service presenting some functionality of the api running Vite, React and TypeScript.

#### Local Deployment

(Requires NPM)
Enter any cli and navigate to the "frontend" directory. Then run:
1. npm install
2. npm run dev -- --open
And it should open in your default browser.
