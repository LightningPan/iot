package com.iot.thingshadowanddevicemanege.service;

import com.iot.thingshadowanddevicemanege.dao.DevicePropertiesDao;
import com.iot.thingshadowanddevicemanege.model.DeviceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DevicePropertiesService {
    private final DevicePropertiesDao devicePropertiesDao;

    @Autowired
    public DevicePropertiesService(DevicePropertiesDao devicePropertiesDao) {
        this.devicePropertiesDao = devicePropertiesDao;
    }

    public Optional<DeviceProperties> findDeviceProperties(long statusId) {
        return devicePropertiesDao.findById(statusId);
    }

    public void updateDeviceStatus(DeviceProperties deviceProperties) {
        devicePropertiesDao.save(deviceProperties);
    }
}
