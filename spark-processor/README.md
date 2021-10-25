# Spark Processor

## Prerequisites

This service uses Spark version 3.1.2 and is built with Scala version 2.12.15. It can therefore also
be run with Java version 11 or version 8.

## Run

Once you have fulfilled the prerequisites, just run the build file of Scala build tools:

`sbt run`

## Kafka

The application requires Kafka to be running and to be reachable on `localhost:9092`
where it subscribes to the topic `tweets-enriched` and publishes on `correlations`.

## Machine Learning

The sentiment of a texts is its attitude, if it is a positive or negative piece of text. Using
sentimental analysis on the texts of our tweets would have allowed us to match the weather data we
attached earlier in the chain to see if it affects the mood of our Tweeters. However, it proved to
be more complex than we imagined getting that part working. So we, instead, opted for a
random `positive/negative` variable. This means that it still has a 50/50 chance of getting it
right!

While we did not get this part to work, we did get the model to load. The machine learning model
uses about 6-7 GB of RAM, which is a lot! If you wanted to load the machine learning module, you
would need more RAM than what Scala gets by default:

`sbt run -J-Xmx4G`