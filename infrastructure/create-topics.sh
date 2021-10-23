#! /bin/bash

docker exec -i kafka /bin/bash <<'EOF'
$KAFKA_HOME/bin/kafka-topics.sh \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 1 \
  --topic tweets
$KAFKA_HOME/bin/kafka-topics.sh \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 1 \
  --topic tweets-enriched
$KAFKA_HOME/bin/kafka-topics.sh \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 1 \
  --topic correlations
exit
EOF