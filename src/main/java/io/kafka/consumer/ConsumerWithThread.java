package io.kafka.consumer;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerWithThread {
	public static void main(String [] args) throws IOException {

		new ConsumerWithThread().run();
		
	}
	
	private ConsumerWithThread() {
		
	}
	
	private void run() throws IOException {
		final Logger logger = LoggerFactory.getLogger(ConsumerWithThread.class);
		String bootstrapServerHost = "127.0.0.1:9092";
		String groupId = "my-sixth-application";
		String topic = "first_topic";
		
		//latch for dealing with multiple threads
		CountDownLatch latch = new CountDownLatch(1);
		logger.info("creating consumer thread");
		Runnable myConsumerRunnable = new ConsumerRunnable(topic, bootstrapServerHost, groupId, latch);
		Thread myThread = new Thread(myConsumerRunnable);
		
		//start the thread
		myThread.start();
		
		//add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			logger.info("caught shutdown hook");
			((ConsumerRunnable) myConsumerRunnable).shutdown();
			try {
				latch.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("shutodown done");
		}
		));
		
		try {
			latch.wait();
		} catch (InterruptedException e) {
			logger.error("application interrupted "+e);
			e.printStackTrace();
		}finally {
			logger.info("application closing");
		}
		
		  System.in.read();
		    System.exit(0);
	}
		
		class ConsumerRunnable implements Runnable{
			
			private CountDownLatch latch;
			KafkaConsumer<String, String> consumer;
			private final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);
			
			public ConsumerRunnable(String topic, String bootstrapServerHost,String groupId,CountDownLatch latch) {
				this.latch = latch;
				
				Properties properties = new Properties();
				
				properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerHost);
				
				//consumer convers bytes to string which is called deserialization
				properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
				properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
				properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
				properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
				
				consumer = new KafkaConsumer<String, String>(properties);
				
				consumer.subscribe(Arrays.asList(topic));
			}
			
			public void shutdown() {
				
				//it will interrupt consumer.poll()
				//it will throw the WakeupException
				consumer.wakeup();
			}

			public void run() {
				
				try {
						while(true) {
							ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
							
							for(ConsumerRecord<String,String> record: records) {
								logger.info("key:" + record.key() + "value: "+ record.value());
								logger.info("Partition : "+record.partition()+ "Offset: "+record.offset());
							}
						}
				}catch(WakeupException e) {
					logger.error("received shutdown signal");
				}finally {
					consumer.close();
					//tell the main code that consumer code is done
					latch.countDown();
					
				}
			
		}
}

}