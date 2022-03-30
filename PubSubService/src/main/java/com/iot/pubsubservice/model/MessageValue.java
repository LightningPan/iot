package com.iot.pubsubservice.model;

import lombok.Data;

@Data
public class MessageValue {
    private String value;
    private String messageId;
    private long time;
}
