package com.iot.thingshadowanddevicemanege.component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.iot.thingshadowanddevicemanege.model.KafkaKey;
import com.iot.thingshadowanddevicemanege.service.DeviceOperationLogService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.iot.thingshadowanddevicemanege.model.KafkaKey.Type.*;

@Component
public class KafkaConsumer {

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ThingShadowProcessor thingShadowProcessor;
    private final DeviceOperationLogService deviceOperationService;

    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(50000), new ThreadFactoryBuilder().setNameFormat("TpThreadPoll-%d").build());

    @Autowired
    public KafkaConsumer(ThingShadowProcessor thingShadowProcessor, DeviceOperationLogService deviceOperationService) {
        this.thingShadowProcessor = thingShadowProcessor;
        this.deviceOperationService = deviceOperationService;
    }

    @KafkaListener(topics = "broker_to_shadow_topic", groupId = "testGroup")
    public void onMessage(ConsumerRecord<KafkaKey, String> message) {
        try {
            executorService.submit(() -> processMessage(message));
        } catch (Exception e) {
            logger.info("submit task occurs exception ", e);
        }
    }

    private void processMessage(ConsumerRecord<KafkaKey, String> message) {
        KafkaKey key = message.key();
        if (key.getMessageType() == StatusUpload) {
            thingShadowProcessor.updateDeviceStatus(key.getProductId(), key.getDeviceName(), message.value());
        } else if (key.getMessageType() == StatusReply) {
            thingShadowProcessor.updateDeviceStatus(key.getProductId(), key.getDeviceName(), message.value());
        } else if (key.getMessageType() == PropertiesReply) {
            thingShadowProcessor.updateDeviceProperties(key.getProductId(), key.getDeviceName(), key.getMessageId(), message.value(), true, true);
        } else if (key.getMessageType() == PropertiesUpload) {
            thingShadowProcessor.updateDeviceProperties(key.getProductId(), key.getDeviceName(), key.getMessageId(), message.value(), false, true);
        } else if (key.getMessageType() == OnlineStatus) {
            thingShadowProcessor.processOnlineStatus(key.getProductId(), key.getDeviceName(), message.value(), key.getTime());
        }
        if (key.getMessageId() != null) {
            deviceOperationService.updateLog(key.getMessageId(), message.value(), key.getTime());
        }
    }
}
