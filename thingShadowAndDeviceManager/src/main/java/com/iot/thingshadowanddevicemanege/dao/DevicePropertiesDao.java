package com.iot.thingshadowanddevicemanege.dao;

import com.iot.thingshadowanddevicemanege.model.DeviceProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicePropertiesDao extends JpaRepository<DeviceProperties, Long> {
}
