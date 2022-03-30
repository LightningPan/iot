package com.iot.thingshadowanddevicemanege.controller;

import com.iot.thingshadowanddevicemanege.service.DeviceOperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/operation")
public class DeviceOperationController {

    private final DeviceOperationService deviceOperationService;

    public DeviceOperationController(DeviceOperationService deviceOperationService) {
        this.deviceOperationService = deviceOperationService;
    }

    @GetMapping("/getStatus")
    public void getStatus(@RequestParam Long productId,
                          @RequestParam String deviceName) {
        deviceOperationService.getStatus(productId, deviceName);
    }

    @PostMapping("/setProperties")
    public ResponseEntity<String> setProperties(@RequestParam Long productId,
                                                @RequestParam String deviceName,
                                                @RequestBody Map<String, Object> params,
                                                @RequestParam boolean updateShadow) {
        return ResponseEntity.ok(deviceOperationService.setProperties(productId, deviceName, params, updateShadow));
    }

    @GetMapping("/getProperties")
    public void getProperties(@RequestParam Long productId,
                              @RequestParam String deviceName) {
        deviceOperationService.getProperties(productId, deviceName);
    }

}
