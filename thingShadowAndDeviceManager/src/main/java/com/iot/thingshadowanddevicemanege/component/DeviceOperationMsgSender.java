package com.iot.thingshadowanddevicemanege.component;

import com.google.gson.Gson;
import com.iot.thingshadowanddevicemanege.model.KafkaKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class DeviceOperationMsgSender {

    private final static Logger logger = LoggerFactory.getLogger(DeviceOperationMsgSender.class);

    private final KafkaTemplate<KafkaKey, String> kafkaTemplate;

    private final String topic = "shadow_to_broker_topic";

    @Autowired
    public DeviceOperationMsgSender(KafkaTemplate<KafkaKey, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(KafkaKey key, String value) {
        ListenableFuture<SendResult<KafkaKey, String>> future = kafkaTemplate.send(topic, key, value);
        future.addCallback(success -> logger.info("send success"), fail -> logger.info("send false, key: " + new Gson().toJson(key) + " value: " + value));
    }
}
