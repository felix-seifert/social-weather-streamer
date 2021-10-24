# analytics-indicator - a.k.a. frontend

This service represents the frontend of the Social Weather Streamer. It listens to the
topic `correlation` and shows the average over all received correlations in the browser
on `http://localhost::8080`.

## Prerequisites

This service requires Java 11. In addition, Maven is needed because its wrapper does not seem to
work.

## Run in Production

To create a production build, call `mvn clean package -Pproduction`. This will build a JAR file
with all the dependencies and front-end resources, ready to be deployed. The file can be found in
the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/socialweatherstreamer-1.0-SNAPSHOT.jar`
