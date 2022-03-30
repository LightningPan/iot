package com.iot.pubsubservice.publish;

import com.iot.pubsubservice.config.PubSubConfig;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQTTPub {
    private final PubSubConfig pubSubConfig;

    private MqttAsyncClient mqttAsyncClient;

    @Autowired
    public MQTTPub(PubSubConfig pubSubConfig) {
        this.pubSubConfig = pubSubConfig;
    }

    private MqttAsyncClient getClient() throws MqttException {
        if (mqttAsyncClient == null) {
            synchronized (MqttAsyncClient.class) {
                if (mqttAsyncClient == null) {
                    mqttAsyncClient = new MqttAsyncClient(pubSubConfig.getHost(), "pub");
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    options.setConnectionTimeout(10);
                    options.setKeepAliveInterval(20);
                    options.setAutomaticReconnect(true);
                    options.setMaxInflight(200);
                    mqttAsyncClient.connect(options).waitForCompletion();
                }
            }
        }
        return mqttAsyncClient;
    }

    public void sendMQTTMsg(String topic, String value) throws MqttException {
        MqttAsyncClient mqttAsyncClient = getClient();
        if (!mqttAsyncClient.isConnected()) {
            mqttAsyncClient = null;
            return;
        }
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(1);
        mqttMessage.setPayload(value.getBytes());
        mqttAsyncClient.publish(topic, mqttMessage);
    }
}
