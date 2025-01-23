Kafka Config
- If Kafka is deployed in Docker
  - Make sure have kafka confluence downloaded on local machine
  - kafka-topics.sh --alter --bootstrap-server [your-kafka-broker]:9092 --partitions [new-num-partitions] --topic [your-topic-name]
  - 
  - 
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 2 --topic elr_action_tracker
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_duplicate
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_edx_log
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_processing_handle_lab
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_processing_public_health_case
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_unprocessed
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_raw
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_raw_dlt
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_unprocessed
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_validated
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic elr_validated_dlt
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic xml_converted
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic xml_prep
kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 10 --topic xml_prep_dlt_manual


https://stackoverflow.com/questions/65744538/problems-with-amazon-msk-default-configuration-and-publishing-with-transactions




CONFLUENCE KAFKA

docker exec -t broker kafka-topics --bootstrap-server broker:29092 --topic elr_unprocessed  --describe
docker exec -t broker kafka-topics --bootstrap-server broker:29092 --topic elr_processing_handle_lab  --describe
docker exec -t broker kafka-topics --bootstrap-server broker:29092 --topic elr_processing_public_health_case  --describe
docker exec -t broker kafka-topics --bootstrap-server broker:29092 --topic elr_edx_log  --describe
docker exec -t broker kafka-topics --bootstrap-server broker:29092 --topic elr_action_tracker  --describe


docker exec -t broker kafka-topics -alter --bootstrap-server broker:29092 --topic dp_elr_unprocessed --partitions 100
docker exec -t broker kafka-topics -alter --bootstrap-server broker:29092 --topic dp_elr_processing_handle_lab --partitions 100
docker exec -t broker kafka-topics -alter --bootstrap-server broker:29092 --topic dp_elr_processing_public_health_case --partitions 100
docker exec -t broker kafka-topics -alter --bootstrap-server broker:29092 --topic dp_elr_edx_log --partitions 100
docker exec -t broker kafka-topics -alter --bootstrap-server broker:29092 --topic dp_elr_action_tracker --partitions 100

