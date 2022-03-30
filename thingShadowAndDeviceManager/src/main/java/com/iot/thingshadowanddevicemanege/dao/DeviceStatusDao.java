package com.iot.thingshadowanddevicemanege.dao;

import com.iot.thingshadowanddevicemanege.model.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceStatusDao extends JpaRepository<DeviceStatus, Long> {
}
