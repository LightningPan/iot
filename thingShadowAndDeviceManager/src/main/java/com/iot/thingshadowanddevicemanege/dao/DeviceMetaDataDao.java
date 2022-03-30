package com.iot.thingshadowanddevicemanege.dao;

import com.iot.thingshadowanddevicemanege.model.DeviceMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceMetaDataDao extends JpaRepository<DeviceMetaData, Long> {
    DeviceMetaData findByDeviceNameAndProductId(String deviceName, Long productId);
    Page<DeviceMetaData> findByProductId(Long productId, Pageable pageable);
}
