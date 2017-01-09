package com.dingwen.kafka.custompartitioner;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
 
public class KafkaUserCustomPatitioner implements Partitioner {
  private IUserService userService;
 
  public KafkaUserCustomPatitioner() {
    userService = new UserServiceImpl();
  }
 
  @Override
  public void configure(Map<String, ?> configs) {
 
  }
 
  @Override
  public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes,
      Cluster cluster) {
 
    int partition = 0;
    String userName = (String) key;
    // Find the id of current user based on the username
    Integer userId = userService.findUserId(userName);
    
   // int noOfPartitions = cluster.topics().size();
    List partitions = cluster.availablePartitionsForTopic(topic);    
    // If the userId not found, default partition is 0
    if (userId != null && userId <= partitions.size()-1) {
      partition = userId;
    } 
    return partition;
  }
 
  @Override
  public void close() {
 
  }
 
}
 