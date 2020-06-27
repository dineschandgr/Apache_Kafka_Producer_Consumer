# Apache_Kafka_Producer_Consumer
Apache Kakfa Producer Consumer using Java 8

This is a producer consumer application using Kafka

Prerequsities;

1. Download Zookeeper. Configure the config/zookeeper.properties file. zookeeper is required for kafka to run. Zookeeper will store the kafka configurations
2. Download Kafka. Configure the config/server.properties

#zookeeper start

zookeeper-server-start.bat config/zookeeper.properties

runs on port 2181

#kafka start

kafka-server-start.bat config/server.properties

#create multiple copies of server.properties and run them separately for multi broker kafka application

runs on port 9092

#Steps

1. Create a topic
2. Configure the producer with bootstrapServerHost = "127.0.0.1:9092";
3. Create a producer with key ProducerRecord<String,String> record = new ProducerRecord<String, String>(topic,key,value);
   All the records with same key goes to the same partition
4. Configure the consumer with bootstrapServerHost = "127.0.0.1:9092";
5. Subscribe consumer to the topic
6. Run the producer application to publish the data to kafka
7. Run the consumer application to consume the data from kafka


