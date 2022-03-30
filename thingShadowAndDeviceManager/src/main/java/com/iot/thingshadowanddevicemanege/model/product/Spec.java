package com.iot.thingshadowanddevicemanege.model.product;

import lombok.Data;

@Data
public class Spec {
    /**
     * numeric
     * max 最大值
     * min 最小值
     * step 步长
     */
    String max;
    String min;
    String step;

    /**
     * array
     * size 数组最大长度
     * type 数据类型
     */
    int size;
    DataType type;

    /**
     * struct
     * items 子项，不可嵌套新的struct
     */
    ThingModel[] items;

    /**
     * ENUM类型
     */
    String[] enums;
}
