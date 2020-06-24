package io.kafka.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {

	public static void main(String [] args) {

		String bootstrapServerHost = "127.0.0.1:9092";
		Properties properties = new Properties();
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerHost);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//create kafka producer with the config
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		
		ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","hello world");
		
		//aynchronous
		producer.send(record);
		
		producer.flush();
		producer.close();
	}
}
