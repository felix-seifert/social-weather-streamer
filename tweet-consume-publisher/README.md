# tweet-consume-publisher

This services connects to Twitter's
[Filtered Stream](https://developer.twitter.com/en/docs/twitter-api/tweets/filtered-stream)
API. It pulls Tweets which were just published and match the rules in the class
[TwitterListener](src/main/java/com/felixseifert/socialweatherstreamer/tweetconsumepublisher/TwitterListener.java)
The service then publishes the Tweets with a geolocation to the local Kafka topic _tweets_.
Basically, it populates the topic _tweets_ with live Tweets which are about a few cities.

## Prerequisites

To start and interact with Kafka, have a look at the folder [infrastructure](../infrastructure). To
consume Twitter APIs, you would have to
[apply for a developer account](https://developer.twitter.com/en/apply-for-access) and insert the
Bearer token into the file [application.properties](src/main/resources/application.properties).

As this service was build with the native compilation feature of GraalVM in mind, the service
requires at least Java version 11.

## Run

### Development Mode

To run the service in Quarkus development mode without the need to separate build and run, you can
use the Quarkus plugin via the Maven wrapper.

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Production Mode

To create a `.jar` of the `tweet-consume-publisher` application, you have to package the whole
application with the Maven Wrapper.

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

> :x: Currently, this service does not parse Tweets correctly when it runs in native mode, it just publishes empty messages to Kafka. Please help in debugging and/or run the service in the JVM [production mode](#production-mode).

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

You can then execute your native executable
with `./target/tweet-consume-publisher-1.0-SNAPSHOT-runner`.

```shell script
./target/tweet-consume-publisher-1.0-SNAPSHOT-runner
```

If you want to learn more about building native executables, please consult
[https://quarkus.io/guides/maven-tooling.html](https://quarkus.io/guides/maven-tooling.html).

## Data Model

The following Tweet represents an example of how a Tweet is structured when it is published to the
topic `tweets`.

```json
{
  "id": 1451602237835382807,
  "text": "Consume the consommé (@ Casa Birria NYC in New York, NY) https://t.co/1Tc5YgmwHf",
  "createdAt": "2021-10-22T17:31:57.000Z",
  "geoInformation": {
    "id": "01a9a39529b27f36",
    "fullName": "Manhattan, NY",
    "country": "United States",
    "countryCode": "US",
    "boxCoordinates": [
      -74.026675,
      40.683935,
      -73.910408,
      40.877483
    ],
    "type": "city"
  }
}
```
