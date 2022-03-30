package com.iot.thingshadowanddevicemanege.controller;

import com.iot.thingshadowanddevicemanege.model.DeviceProperties;
import com.iot.thingshadowanddevicemanege.model.DeviceStatus;
import com.iot.thingshadowanddevicemanege.service.ThingShadowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/thingShadow")
public class ThingShadowController {
    private final ThingShadowService thingShadowService;

    @Autowired
    public ThingShadowController(ThingShadowService thingShadowService) {
        this.thingShadowService = thingShadowService;
    }

    @GetMapping("/queryThingShadowStatus")
    public ResponseEntity<DeviceStatus> queryThingShadowStatus(@RequestParam Long productId,
                                                               @RequestParam String deviceName) {
        return ResponseEntity.ok(thingShadowService.queryThingShadowStatus(productId, deviceName));
    }

    @GetMapping("/queryThingShadowProperties")
    public ResponseEntity<DeviceProperties> queryThingShadowProperties(@RequestParam Long productId,
                                                                       @RequestParam String deviceName) {
        return ResponseEntity.ok(thingShadowService.queryThingShadowProperties(productId, deviceName));
    }
}
