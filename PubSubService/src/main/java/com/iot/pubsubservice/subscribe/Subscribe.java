package com.iot.pubsubservice.subscribe;

import com.google.gson.Gson;
import com.iot.pubsubservice.component.KafkaMsgSender;
import com.iot.pubsubservice.config.PubSubConfig;
import com.iot.pubsubservice.model.KafkaKey;
import com.iot.pubsubservice.model.MessageValue;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Subscribe {
    private final PubSubConfig pubSubConfig;

    /**
     * 服务端订阅:
     * $SYS/brokers/${node}/clients/#  设备上下线
     * products/# 订阅所有设备相关的消息
     * products/${productId}/${deviceName}/StatusUpload 设备主动上传消息
     * products/${productId}/${deviceName}/StatusReply  设备获取到拉去状态消息后主动回复
     * products/${productId}/${deviceName}/PropertiesReply  设备回复设置信息
     * products/${productId}/${deviceName}/PropertiesUpload  设备主动上报设置信息
     *
     * 设备订阅时, clientId为${productId}@${deviceName}
     * device/${productId}/${deviceName} 设备订阅的topic
     */
    private final String[] topics = new String[]{
            "$SYS/brokers/emqx@127.0.0.1/clients/#",
            "product/#"
    };

    private final KafkaMsgSender kafkaMsgSender;

    private final MqttCallback mqttStatusCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            KafkaKey key = new KafkaKey();
            key.setMessageId("");
            key.setMessageType(KafkaKey.Type.OnlineStatus);
            String[] infos = topic.split("/");
            String value = null;
            long time = 0;
            if (infos[5].equals("connected")) {
                value = "true";
                time = (long) new JacksonJsonParser().parseMap(new String(mqttMessage.getPayload())).get("connected_at");
            } else if (infos[5].equals("disconnected")) {
                value = "false";
                time = (long) new JacksonJsonParser().parseMap(new String(mqttMessage.getPayload())).get("disconnected_at");
            }
            String[] info = infos[4].split("@");
            key.setProductId(Long.parseLong(info[0]));
            key.setDeviceName(info[1]);
            key.setTime(time);
            kafkaMsgSender.sendMessage(key, value);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    };

    private final MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable throwable) {

        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            String[] infos = topic.split("/");
            Long productId = Long.parseLong(infos[1]);
            String deviceName = infos[2];
            String type = infos[3];
            MessageValue messageValue = new Gson().fromJson(new String(mqttMessage.getPayload()), MessageValue.class);
            KafkaKey key = new KafkaKey();
            key.setMessageId(messageValue.getMessageId());
            key.setTime(messageValue.getTime());
            key.setProductId(productId);
            key.setDeviceName(deviceName);
            key.setMessageType(KafkaKey.Type.valueOf(type));
            kafkaMsgSender.sendMessage(key, messageValue.getValue());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    };

    @Autowired
    public Subscribe(PubSubConfig pubSubConfig, KafkaMsgSender kafkaMsgSender) {
        this.pubSubConfig = pubSubConfig;
        this.kafkaMsgSender = kafkaMsgSender;
    }

    public void subTopics() throws MqttException {
        List<MqttAsyncClient> list = new ArrayList<>();
        for (String topic : topics) {
            if (topic.equals("$SYS/brokers/emqx@127.0.0.1/clients/#")) {
                list.add(subTopic("sub1", 1, topic, mqttStatusCallback));
            } else {
                subTopic("sub2", 1, topic, mqttCallback);
            }
        }
    }

    private MqttAsyncClient subTopic(String clientId, int qos, String topic, MqttCallback mqttCallback) throws MqttException {
        MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(pubSubConfig.getHost(), clientId);
        mqttAsyncClient.setCallback(mqttCallback);
        mqttAsyncClient.connect(getOptions("", "")).waitForCompletion();
        mqttAsyncClient.subscribe(topic, qos);
        return mqttAsyncClient;
    }

    private MqttConnectOptions getOptions(String username, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setConnectionTimeout(10);
        mqttConnectOptions.setKeepAliveInterval(60);
        mqttConnectOptions.setAutomaticReconnect(true);
        return mqttConnectOptions;
    }

}
