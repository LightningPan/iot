package com.iot.thingshadowanddevicemanege.service;

import com.iot.thingshadowanddevicemanege.dao.DeviceStatusDao;
import com.iot.thingshadowanddevicemanege.model.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceStatusService {

    private final DeviceStatusDao deviceStatusDao;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DeviceStatusService(DeviceStatusDao deviceStatusDao, RedisTemplate<String, String> redisTemplate) {
        this.deviceStatusDao = deviceStatusDao;
        this.redisTemplate = redisTemplate;
    }

    public Optional<DeviceStatus> findDeviceStatus(long statusId) {
        return deviceStatusDao.findById(statusId);
    }

    public void updateDeviceStatus(DeviceStatus deviceStatus) {
        deviceStatusDao.save(deviceStatus);
    }
}
