package io.kafka.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerWithCallBack {

	public static void main(String [] args) {

		final Logger logger = LoggerFactory.getLogger(ProducerWithCallBack.class);
		
		String bootstrapServerHost = "127.0.0.1:9092";
		Properties properties = new Properties();
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerHost);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//create kafka producer with the config
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		
		for(int i =1;i<10;i++) {
			ProducerRecord<String,String> record = new ProducerRecord<String, String>("first_topic","hello world"+i);
			
			//aynchronous
			producer.send(record,new Callback() {
				
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					//executes every time a records is sent or exception is thrown
					if(exception == null) {
						logger.info("metadata : \n"+
						"Topic: "+metadata.topic() + "\n" +
						"Partition: "+metadata.partition() + "\n" +
						"Offset: "+metadata.offset() + "\n" +
						"Timestamp: "+metadata.timestamp());
					}else {
						logger.error("error in producing "+exception);
					}
				}
			});
		
		}
		producer.flush();
		producer.close();
	}
}
