package com.dingwen.kafka.custompartitioner;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class UserProducerThread implements Runnable {

	private final KafkaProducer<String, String> producer;
	private final String topic;
	private IUserService userService;

	public UserProducerThread(String brokers, String topic) {
		Properties prop = createProducerConfig(brokers);
		this.producer = new KafkaProducer<String, String>(prop);
		this.topic = topic;
		userService = new UserServiceImpl();
	}

	private static Properties createProducerConfig(String brokers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("partitioner.class", "com.dingwen.kafka.custompartitioner.KafkaUserCustomPatitioner");
		return props;
	}

	@Override
	public void run() {
		System.out.println("Produces 5 messages");
		List<String> users = userService.findAllUsers();
		for (String user : users) {

			String msg = "Hello " + user;

			//synchronous send
//			try {
//				Future<RecordMetadata> future = producer.send(new ProducerRecord<String, String>(topic, user, msg));
//			RecordMetadata metadata = future.get();
//			System.out.println("message is sent to partition number:" + metadata.partition() + " and offset " + metadata.offset());
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			} catch (ExecutionException e1) {
//				e1.printStackTrace();
//			}
			
			// asynchronous send
			Future<RecordMetadata>  futureAsyn = producer.send(new ProducerRecord<String, String>(topic, user, msg), new Callback() {
																								
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					}
					System.out.println("Sent:" + msg + ", User: " + user + ", Partition: " + metadata.partition());
				}
			});
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// closes producer
		producer.close();

	}
}
