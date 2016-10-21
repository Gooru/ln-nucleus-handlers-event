package org.gooru.nucleus.handlers.events.processors;

import io.vertx.core.json.JsonObject;

import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.SerializationException;
import org.gooru.nucleus.handlers.events.app.components.KafkaRegistry;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageDispatcher {
    private static final MessageDispatcher INSTANCE = new MessageDispatcher();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDispatcher.class);

    private MessageDispatcher() {
    }

    public static MessageDispatcher getInstance() {
        return INSTANCE;
    }

  public void sendMessage2Kafka(String eventName, JsonObject eventBody) {
      //
      // Kafka message publish
      //
      if (KafkaRegistry.getInstance().testWithoutKafkaServer()) {
          return; // running without KafkaServer...
      }
      
      sendMessageToKafka(KafkaRegistry.getInstance().getKafkaTopic(), eventName, eventBody);
      String enricherTopic = KafkaRegistry.getInstance().getContentEnricherTopic();
      
      if ((enricherTopic != null) && !enricherTopic.isEmpty()) {
        // need to check if this message needs to go on enricher topic as well
        if (isEventOfInterestForTagging(eventName)) {
          LOGGER.debug("Found an event of interest for tagging handler. Will post this on enricherTopic.");
          sendMessageToKafka(KafkaRegistry.getInstance().getContentEnricherTopic(), eventName, eventBody);
        }
      }
  }
  
  private void sendMessageToKafka(String topic, String eventName, JsonObject eventBody) {
    Producer<String, String> producer = KafkaRegistry.getInstance().getKafkaProducer();
    ProducerRecord<String, String> kafkaMsg;
    
    kafkaMsg = new ProducerRecord<>(topic, eventName, eventBody.toString());
    
    try {
        if (producer != null) {
            producer.send(kafkaMsg, (metadata, exception) -> {
                if (exception == null) {
                    LOGGER.info("Message Delivered Successfully: Offset : " + metadata.offset() + " : Topic : "
                        + metadata.topic() + " : Partition : " + metadata.partition() + " : Message : " + kafkaMsg);
                } else {
                    LOGGER.error("Message Could not be delivered : " + kafkaMsg + ". Cause: " + exception.getMessage());
                }
            });
            LOGGER.debug("Message Sent Successfully: " + kafkaMsg);
        } else {
            LOGGER.error("Not able to obtain producer instance");
        }
    } catch (InterruptException | BufferExhaustedException | SerializationException ie) {
        // - If the thread is interrupted while blocked
        LOGGER.error("sendMessageToKafka: to Kafka server:", ie);
    }    
  }
    
  private boolean isEventOfInterestForTagging(String eventName){
    switch (eventName) {
      case MessageConstants.MSG_OP_EVT_COURSE_CREATE:
      case MessageConstants.MSG_OP_EVT_COURSE_UPDATE:
      case MessageConstants.MSG_OP_EVT_RESOURCE_CREATE:
      case MessageConstants.MSG_OP_EVT_RESOURCE_UPDATE:
      case MessageConstants.MSG_OP_EVT_COLLECTION_CREATE:
      case MessageConstants.MSG_OP_EVT_COLLECTION_UPDATE:
        return true;
      default: 
        return false;
    }
  }
  
}
