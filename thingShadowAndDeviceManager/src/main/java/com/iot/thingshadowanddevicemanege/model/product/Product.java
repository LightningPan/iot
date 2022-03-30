package com.iot.thingshadowanddevicemanege.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String name;

    Long gmtModified;

    Long gmtCreate;

    @Column(length = 1024)
    String properties;

    boolean encode;

    boolean decode;

}
