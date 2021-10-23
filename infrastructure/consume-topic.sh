#! /bin/bash

docker exec -i kafka /bin/bash <<'EOF'
$KAFKA_HOME/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic correlations \
  --from-beginning 
exit
EOF
