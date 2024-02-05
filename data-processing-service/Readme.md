Kafka Config
- If Kafka is deployed in Docker
  - Make sure have kafka confluence downloaded on local machine
  - kafka-topics.sh --alter --bootstrap-server [your-kafka-broker]:9092 --partitions [new-num-partitions] --topic [your-topic-name]
