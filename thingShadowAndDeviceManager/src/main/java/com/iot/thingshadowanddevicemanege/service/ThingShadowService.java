package com.iot.thingshadowanddevicemanege.service;

import com.iot.thingshadowanddevicemanege.model.DeviceMetaData;
import com.iot.thingshadowanddevicemanege.model.DeviceProperties;
import com.iot.thingshadowanddevicemanege.model.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingShadowService {
    private final DeviceMetaDataService deviceMetaDataService;
    private final DeviceStatusService deviceStatusService;
    private final DevicePropertiesService devicePropertiesService;

    @Autowired
    public ThingShadowService(DeviceMetaDataService deviceMetaDataService, DeviceStatusService deviceStatusService, DevicePropertiesService devicePropertiesService) {
        this.deviceMetaDataService = deviceMetaDataService;
        this.deviceStatusService = deviceStatusService;
        this.devicePropertiesService = devicePropertiesService;
    }

    public DeviceStatus queryThingShadowStatus(Long productId, String deviceName) {
        DeviceMetaData deviceMetaData = deviceMetaDataService.find(deviceName, productId, null, null).get(0);
        if (deviceMetaData == null) {
            throw new RuntimeException("设备不存在");
        }
        return deviceStatusService.findDeviceStatus(deviceMetaData.getDeviceStatusId()).orElse(null);
    }

    public DeviceProperties queryThingShadowProperties(Long productId, String deviceName) {
        DeviceMetaData deviceMetaData = deviceMetaDataService.find(deviceName, productId, null, null).get(0);
        if (deviceMetaData == null) {
            throw new RuntimeException("设备不存在");
        }
        return devicePropertiesService.findDeviceProperties(deviceMetaData.getDevicePropertyId()).orElse(null);
    }
}
