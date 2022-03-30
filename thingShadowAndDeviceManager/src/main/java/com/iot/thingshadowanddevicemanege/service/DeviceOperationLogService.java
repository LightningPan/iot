package com.iot.thingshadowanddevicemanege.service;

import com.iot.thingshadowanddevicemanege.dao.DeviceOperationLogDao;
import com.iot.thingshadowanddevicemanege.model.DeviceOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceOperationLogService {
    private final DeviceOperationLogDao deviceOperationLogDao;

    @Autowired
    public DeviceOperationLogService(DeviceOperationLogDao deviceOperationLogDao) {
        this.deviceOperationLogDao = deviceOperationLogDao;
    }

    public void addNewLog(DeviceOperationLog deviceOperationLog) {
        deviceOperationLogDao.save(deviceOperationLog);
    }

    public void updateLog(String id, String replyValue, long replyTime) {
        DeviceOperationLog deviceOperationLog = deviceOperationLogDao.findById(id).orElse(null);
        if (deviceOperationLog == null) {
            throw new RuntimeException("没有这条记录");
        }
        deviceOperationLog.setReplyTime(replyTime);
        deviceOperationLog.setReplyValue(replyValue);
        deviceOperationLogDao.save(deviceOperationLog);
    }
}
