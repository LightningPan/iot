package com.iot.thingshadowanddevicemanege.service;

import com.iot.thingshadowanddevicemanege.Exception.RequestException;
import com.iot.thingshadowanddevicemanege.dao.DeviceMetaDataDao;
import com.iot.thingshadowanddevicemanege.dao.DevicePropertiesDao;
import com.iot.thingshadowanddevicemanege.dao.DeviceStatusDao;
import com.iot.thingshadowanddevicemanege.model.DeviceMetaData;
import com.iot.thingshadowanddevicemanege.model.DeviceProperties;
import com.iot.thingshadowanddevicemanege.model.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class DeviceMetaDataService {

    private final DeviceMetaDataDao deviceMetaDataDao;
    private final DeviceStatusDao deviceStatusDao;
    private final DevicePropertiesDao devicePropertiesDao;

    @Autowired
    public DeviceMetaDataService(DeviceMetaDataDao deviceMetaDataDao, DeviceStatusDao deviceStatusDao, DevicePropertiesDao devicePropertiesDao) {
        this.deviceMetaDataDao = deviceMetaDataDao;
        this.deviceStatusDao = deviceStatusDao;
        this.devicePropertiesDao = devicePropertiesDao;
    }

    public DeviceMetaData updateMetaData(long id, String metaData) {
        Map<String, Object> map = new GsonJsonParser().parseMap(metaData);
        Optional<DeviceMetaData> deviceMetaData = deviceMetaDataDao.findById(id);
        if (deviceMetaData.isPresent()) {
            deviceMetaData.get().setMetaData(metaData);
            deviceMetaData.get().setGmtModified(Calendar.getInstance().getTimeInMillis());
            return deviceMetaDataDao.save(deviceMetaData.get());
        } else {
            throw new RequestException(HttpStatus.BAD_REQUEST, "设备不存在!");
        }
    }

    @Transactional
    public DeviceMetaData addNewDevice(DeviceMetaData deviceMetaData) {
        long time = Calendar.getInstance().getTimeInMillis();
        if (deviceMetaData.getProductId() != null) {
            throw new RequestException(HttpStatus.BAD_REQUEST, "产品不能为空!");
        } else if (deviceMetaData.getDeviceName() != null) {
            throw new RequestException(HttpStatus.BAD_REQUEST, "设备名不能为空!");
        }
        DeviceStatus deviceStatus = new DeviceStatus();
        deviceStatus.setGmtModified(time);
        deviceStatus.setLastOnlineStatus(0);
        deviceStatus.setValues("");
        deviceMetaData.setDeviceStatusId(deviceStatusDao.save(deviceStatus).getId());

        DeviceProperties deviceProperties = new DeviceProperties();
        deviceProperties.setGmtModified(time);
        deviceProperties.setValues("");
        deviceProperties.setModifiedAfterOffline(false);
        deviceProperties.setSuccess("");
        deviceMetaData.setDevicePropertyId(devicePropertiesDao.save(deviceProperties).getId());

        deviceMetaData.setGmtModified(time);
        deviceMetaData.setGmtCreateTime(time);
        deviceMetaData.setOnlineStatus(false);
        return deviceMetaDataDao.save(deviceMetaData);
    }

    public List<DeviceMetaData> find(String deviceName, Long productId, Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (deviceName == null) {
            Page<DeviceMetaData> resPage = deviceMetaDataDao.findByProductId(productId, Pageable.ofSize(size).withPage(page));
            return resPage.getContent();
        } else {
            List<DeviceMetaData> res = new ArrayList<>();
            res.add(deviceMetaDataDao.findByDeviceNameAndProductId(deviceName, productId));
            return res;
        }
    }
}
