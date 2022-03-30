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
public class DeviceMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceName;

    private String deviceType;

    private Long gmtCreateTime;

    private Long gmtModified;

    private Long devicePropertyId;

    private Long deviceStatusId;

    private String metaData;

    private Long productId;

    private boolean onlineStatus;

}
