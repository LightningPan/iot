package com.iot.thingshadowanddevicemanege.model.product;

import lombok.Data;

@Data
public class ThingModel {
    String id;
    String name;
    AccessMode accessMode;
    boolean required;
    DataType dataType;

    public enum AccessMode {
        R,
//        W,
        RW,
    }
}
