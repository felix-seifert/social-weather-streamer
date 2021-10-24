# Infrastructure

This application requires Apache Kafka. For coordination purposes, it also needs Apache Zookeeper.
Both services can be started with Docker Compose.

In addition, a local installation of Apache Spark is needed because it is currently not included as
a Docker container.

After starting Kafka and Zookeeper, the required topics have to be created with the
script `create-topics.sh`.

```shell
docker-compose up
./create-topics.sh
```

The containers would be available under the names `kafka` and `zookeeper`. It is possible to simply
spawn a pseudo-TTY with Bash in the `kafka` container to interact with Kafka.

```shell
docker exec -it kafka /bin/bash
```

In the TTY, you can interact normally with Kafka. The environment variable `$KAFKA_HOME` is already
set. Zookeeper does not run on the same host as Kafka and can therefore not be referred to
with `localhost:2181`. Zookeeper is on the domain `zookeeper` with the port `2181`. Kafka on the
domain `kafka` with port `9092`.

* To **create a new topic** on Kafka, run the following command in the Docker pseudo-TTY.
    ```shell
    $KAFKA_HOME/bin/kafka-topics.sh --create \
        --zookeeper zookeeper:2181 \
        --replication-factor 1 \
        --partitions 1 \
        --topic <<topic-name>>
    ```
* To **list all topics** on Kafka, run the following command in the Docker pseudo-TTY.
    ```shell
    $KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper zookeeper:2181
    ```
* Test Kafka
    * by **producing** messages (type after executing following command to publish messages
      to `<<topic-name>>`)
      ```shell
      $KAFKA_HOME/bin/kafka-console-producer.sh \
        --broker-list localhost:9092 \
        --topic <<topic-name>>
      ```
    * and **consuming** messages (following commands reads all messages from topic `<<topic-name>>`)
      ```shell
      $KAFKA_HOME/bin/kafka-console-consumer.sh \
        --bootstrap-server localhost:9092 \
        --topic <<topic-name>> \
        --from-beginning
      ```
