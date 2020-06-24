package io.kafka.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerWithKeys {

	public static void main(String [] args) throws InterruptedException, ExecutionException {

		final Logger logger = LoggerFactory.getLogger(ProducerWithKeys.class);
		
		String bootstrapServerHost = "127.0.0.1:9092";
		Properties properties = new Properties();
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerHost);
		
		//producer converts string to bytes which is called serialization
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//create kafka producer with the config
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		
		for(int i =1;i<10;i++) {
			
			String topic = "first_topic";
			String value = "Hello World " + Integer.toString(i);
			String key = "id_"+ Integer.toString(i);
			
			//provide a key to make the data go to same partition every time
			ProducerRecord<String,String> record = new ProducerRecord<String, String>(topic,key,value);
			
			logger.info("key :"+key);
			
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
			}).get(); //block the send to make it synchronous. bad practise in production
		
		}
		producer.flush();
		producer.close();
	}
}
