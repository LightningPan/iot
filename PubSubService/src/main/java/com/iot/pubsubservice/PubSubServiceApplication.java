package com.iot.pubsubservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

@EnableConfigurationProperties
@SpringBootApplication
@EnableRetry
public class PubSubServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PubSubServiceApplication.class, args);
    }

}
