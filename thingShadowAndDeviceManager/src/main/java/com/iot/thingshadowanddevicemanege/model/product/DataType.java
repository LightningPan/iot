package com.iot.thingshadowanddevicemanege.model.product;

import lombok.Data;

@Data
public class DataType {
    Type dataType;
    Spec specs;

    public enum Type {
        INT,
        FLOAT,
        DOUBLE,
        LONG,
        ENUM,
        TEXT,
        ARRAY,
        BOOL,
        DATE,
        STRUCT
    }
}
