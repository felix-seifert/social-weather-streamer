# Spark Processor

## Prerequisites

Contrary to the other modules of the project, this part requires Java 8 and will not run with Java 11.
We recommend a tool such as [jenv](https://www.jenv.be/) for managing your java environments (not included).
Alternatively, become a circus master at juggling `JAVA_HOME` environment variables in shells.

## Run


Once you have the correct Java version, just run the build file of Scala build tools:

  `sbt run`

If you wanted to load the machine learning module, you would need more RAM than what Scala gets by default:

  `sbt run -J-Xmx4G`

Wait, machine learning? Read on!


## Kafka

The application requires Kafka to be running and to be reachable on `localhost:9092`
where it subscribes to the topic `tweets-enriched` and publishes on `correlations`.


## Machine Learning

The sentiment of a texts is its attitude, if its a positive or negative piece of text.
Using sentimental analysis on the texts of our tweets would have allowed us to match
the weather data we attached earlier in the chain to see if it affects the mood of our
Tweeters.
However, it proved to be quite more complex than we imagined to get that part working.
So we, instead, opted for a random `positive/negative` variable.
This means it still has a 50/50 of getting it right!


While we did not get this part to work, we did get the model to load.
The machine learning model uses about 6-7GB of RAM, which is a lot!
So, follow the steps up in `Running` for getting that to work in Scala.
