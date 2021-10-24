# Spark Processor

## Running

Contrary to the other parts of the code, this part requires java-8 and will not run with java-11.
We recommend a tool such a [jenv](https://www.jenv.be/) for managing your java environments,
or running the code containers (not included).
Alternatively, become a circus master at juggling `JAVA_HOME` environment variables in shells.
Other than the correct java, the repository above this one contain its `build.sbt`,
the configuration file for the Scala build tools:

  `sbt run`

However, if the machine learning was to be loaded, you'd need more RAM than what Scala gets by default:

  `sbt run -J-Xmx4G`

Wait, machine learning? Read on!


## Kafka

The application requires Kafka to be running and to be reachable over on `localhost:9092`,
where it consumes from `tweets-enriched` and produces `correlated` topics respectively.


## Machine Learning

The sentiment of a texts is its attitude, if its a positive or negative piece of text.
Using sentimental analysis on the texts of our tweets would have allowed us to match
the weather data we attached earlier in the chain to see if it affects the mood of our
Tweeters.
However, it proved to be quite more complex than we imagined to get that part working,
so we instead opted for a random `positive/negative` variable.
This means it still has a 50/50 of getting it right!


While we didn't get this part to work, we did get the model to load.
The machine learning model uses about 6-7GB of RAM, which is a lot!
So, follow the steps up in `Running` for getting that to work in Scala.
