package com.iot.pubsubservice.component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.iot.pubsubservice.model.KafkaKey;
import com.iot.pubsubservice.publish.MQTTPub;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaConsumer {
    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final MQTTPub mqttPub;

    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50000), new ThreadFactoryBuilder().setNameFormat("TpThreadPoll-%d").build());

    public KafkaConsumer(MQTTPub mqttPub) {
        this.mqttPub = mqttPub;
    }

    @KafkaListener(topics = "shadow_to_broker_topic", groupId = "testGroup")
    public void onMessage(ConsumerRecord<KafkaKey, String> message) {
        try {
            executorService.submit(() -> {
                try {
                    processMessage(message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            logger.info("submit task occurs exception ", e);
        }
    }

    @Retryable(
            value = {MqttException.class},
            maxAttempts = 4,
            backoff = @Backoff(
                    delay = 1500,
                    multiplier = 2
            )
    )
    private void processMessage(ConsumerRecord<KafkaKey, String> message) throws MqttException {
        KafkaKey key = message.key();
        String topic = "products/" + key.getProductId() + "/" + key.getDeviceName() + "/" + key.getMessageType();
        String value = message.value();
        mqttPub.sendMQTTMsg(topic, value);
    }

}
