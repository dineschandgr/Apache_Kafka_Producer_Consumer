package io.kafka.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
	public static void main(String [] args) {

		final Logger logger = LoggerFactory.getLogger(Consumer.class);
		String bootstrapServerHost = "127.0.0.1:9092";
		String groupId = "my-fourth-application";
		String topic = "first_topic";
		Properties properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerHost);
		
		//consumer convers bytes to string which is called deserialization
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		//create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
		
		//subscribe consumer to our topic(s)
		consumer.subscribe(Arrays.asList(topic));
		
		//poll for data
		
		while(true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			
			for(ConsumerRecord<String,String> record: records) {
				logger.info("key:" + record.key() + "value: "+ record.value());
				logger.info("Partition : "+record.partition()+ "Offset: "+record.offset());
			}
		}
		
	}
}
