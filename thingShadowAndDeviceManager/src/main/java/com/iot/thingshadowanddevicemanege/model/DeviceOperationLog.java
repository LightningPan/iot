package com.iot.thingshadowanddevicemanege.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "deviceName")})
public class DeviceOperationLog {

    @Id
    private String id;

    private String deviceName;

    private Long productId;

    private String setValue;

    private String replyValue;

    private long setTime;

    private long replyTime;
}
