package com.iot.thingshadowanddevicemanege.controller;

import com.iot.thingshadowanddevicemanege.model.DeviceMetaData;
import com.iot.thingshadowanddevicemanege.service.DeviceMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/deviceMetaData")
public class DeviceMetaDataController {
    private final DeviceMetaDataService deviceMetaDataService;

    @Autowired
    public DeviceMetaDataController(DeviceMetaDataService deviceMetaDataService) {
        this.deviceMetaDataService = deviceMetaDataService;
    }

    @PostMapping("/add")
    public ResponseEntity<DeviceMetaData> addNewDevice(@RequestBody DeviceMetaData deviceMetaData) {
        return ResponseEntity.ok(deviceMetaDataService.addNewDevice(deviceMetaData));
    }

    @PatchMapping("/update")
    public ResponseEntity<DeviceMetaData> updateDeviceMetaData(@RequestBody DeviceMetaData deviceMetaData) {
        return ResponseEntity.ok(deviceMetaDataService.updateMetaData(deviceMetaData.getId(), deviceMetaData.getMetaData()));
    }

    @GetMapping("/find")
    public ResponseEntity<List<DeviceMetaData>> findDeviceMetaData(@RequestParam(required = false) String deviceName,
                                                                   @RequestParam Long productId,
                                                                   @RequestParam(required = false) Integer page,
                                                                   @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(deviceMetaDataService.find(deviceName, productId, page, size));
    }

}
