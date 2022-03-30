package com.iot.thingshadowanddevicemanege.dao;

import com.iot.thingshadowanddevicemanege.model.DeviceOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceOperationLogDao extends JpaRepository<DeviceOperationLog, String> {
}
