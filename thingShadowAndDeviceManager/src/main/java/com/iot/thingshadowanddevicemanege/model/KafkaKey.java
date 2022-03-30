package com.iot.thingshadowanddevicemanege.model;

import lombok.Data;

@Data
public class KafkaKey {
    private Long productId;
    private String deviceName;
    private Type messageType;
    String messageId;
    private long time;

    public enum Type {
        StatusUpload,
        StatusReply,
        PropertiesReply,
        PropertiesUpload,
        OnlineStatus,
    }
}
