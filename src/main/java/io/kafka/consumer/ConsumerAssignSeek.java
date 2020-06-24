package io.kafka.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerAssignSeek {
	public static void main(String [] args) {

		final Logger logger = LoggerFactory.getLogger(ConsumerAssignSeek.class);
		String bootstrapServerHost = "127.0.0.1:9092";
		String topic = "first_topic";
		Properties properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerHost);
		
		//consumer convers bytes to string which is called deserialization
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		//create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		
		//assign and seek are used to replay data or fetch specific message
		
		//assign
		TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
		long offsetToReadFrom = 15L;
		consumer.assign(Arrays.asList(partitionToReadFrom));
		
		//seek
		
		consumer.seek(partitionToReadFrom, offsetToReadFrom);
		
		int noOfMessageToRead = 5;
		boolean keepOnReading = true;
		int noOfMessagesReadSoFar = 0;
		
		while(keepOnReading) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			
			for(ConsumerRecord<String,String> record: records) {
				logger.info("key:" + record.key() + "value: "+ record.value());
				logger.info("Partition : "+record.partition()+ "Offset: "+record.offset());
				noOfMessagesReadSoFar++;

				if(noOfMessagesReadSoFar >= noOfMessageToRead) {
					keepOnReading = false;
					break;
				}
			}
			
		}
		logger.info("exiting consumer");
	}
}
