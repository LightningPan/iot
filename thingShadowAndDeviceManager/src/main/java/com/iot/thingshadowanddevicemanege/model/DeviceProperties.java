package com.iot.thingshadowanddevicemanege.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class DeviceProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String propertyValues;

    private Long gmtModified;

    private boolean modifiedAfterOffline;

    private String messageId;

    private String success;

}
