# weather-enricher Project

The purpose of this service is to enrich the Tweets on the local Kafka topic `tweets` with weather
data. The service then publishes the enriched Tweets to the local Kafka topic `tweets-enriched`.

## Prerequisites

To start and interact with Kafka, have a look at the folder [infrastructure](../infrastructure). To
consume WeatherAPI's data, you would have to
[register for an API key](https://www.weatherapi.com/signup.aspx) and insert the key in the
file [application.properties](src/main/resources/application.properties).

## Run

### Development Mode

To run the service in Quarkus development mode without the need to separate build and run, you can
use the Quarkus plugin via the Maven wrapper.

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Production Mode

To create a `.jar` of the `weather-enricher` application, you have to package the whole application
with the Maven Wrapper.

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory. It can be run like
any other `.jar`.

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```

Be aware that the packaged application is not an _über-jar_ as the dependencies are copied into the
`target/quarkus-app/lib/` directory. If you want to build an _über-jar_, execute the following
command:

```shell script
./mvnw package \
    -Dquarkus.package.type=uber-jar
```

The application is then runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Native Executable

To achieve a fast startup and an application with a smaller footprint, you can run the application
as a native executable. You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you do not have GraalVM installed, you can run the native executable build in a container
using:

```shell script
./mvnw package -Pnative \
    -Dquarkus.native.container-build=true
```

You can then execute your native executable with `./target/weather-enricher-1.0-SNAPSHOT-runner`.

```shell script
./target/weather-enricher-1.0-SNAPSHOT-runner
```

If you want to learn more about building native executables, please consult
[https://quarkus.io/guides/maven-tooling.html](https://quarkus.io/guides/maven-tooling.html).

## Data Model

The following enriched Tweet represents an example of how a Tweets is structured when it is
published to the topic `tweets-enriched`.

```json
{
  "id": 1451847239446327299,
  "text": "With regard to San Francisco health, there is Rule of Law.",
  "createdAt": "2021-10-23T09:45:30.000Z",
  "geoInformation": {
    "id": "47dbb2e661aa176c",
    "fullName": "Hacienda Heights, CA",
    "country": "United States",
    "countryCode": "US",
    "boxCoordinates": [
      -118.037546,
      33.973234,
      -117.927186,
      34.031527
    ],
    "type": "city"
  },
  "weatherInformation": {
    "last_updated": "2021-10-23 02:30",
    "temp_c": 17.8,
    "precip_mm": 0.0,
    "humidity": 75,
    "cloud": 100,
    "feelslike_c": 17.8,
    "is_day": 0
  }
}
```
