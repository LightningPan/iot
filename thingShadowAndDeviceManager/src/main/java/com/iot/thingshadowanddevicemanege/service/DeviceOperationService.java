package com.iot.thingshadowanddevicemanege.service;

import com.google.gson.Gson;
import com.iot.thingshadowanddevicemanege.component.DeviceOperationMsgSender;
import com.iot.thingshadowanddevicemanege.component.ThingShadowProcessor;
import com.iot.thingshadowanddevicemanege.model.DeviceOperationLog;
import com.iot.thingshadowanddevicemanege.model.KafkaKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

@Service
public class DeviceOperationService {

    private final DeviceOperationMsgSender deviceOperationMsgSender;
    private final ThingShadowProcessor thingShadowProcessor;
    private final DeviceOperationLogService deviceOperationLogService;

    @Autowired
    public DeviceOperationService(DeviceOperationMsgSender deviceOperationMsgSender, ThingShadowProcessor thingShadowProcessor, DeviceOperationLogService deviceOperationLogService) {
        this.deviceOperationMsgSender = deviceOperationMsgSender;
        this.thingShadowProcessor = thingShadowProcessor;
        this.deviceOperationLogService = deviceOperationLogService;
    }

    public void getStatus(Long productId, String deviceName) {
        KafkaKey key = new KafkaKey();
        key.setDeviceName(deviceName);
        key.setProductId(productId);
        key.setMessageType(KafkaKey.Type.StatusUpload);
        deviceOperationMsgSender.sendMessage(key, null);
    }

    public void getProperties(Long productId, String deviceName) {
        KafkaKey key = new KafkaKey();
        key.setDeviceName(deviceName);
        key.setProductId(productId);
        key.setMessageType(KafkaKey.Type.PropertiesUpload);
        deviceOperationMsgSender.sendMessage(key, null);
    }

    public String setProperties(Long productId, String deviceName, Map<String, Object> params, boolean updateShadow) {
        String messageId = UUID.randomUUID().toString();
        String value = new Gson().toJson(params);
        if (updateShadow) {
            params = thingShadowProcessor.updateDeviceProperties(productId, deviceName, null, value, false, false);
            value = new Gson().toJson(params);
        }
        KafkaKey key = new KafkaKey();
        key.setMessageId(messageId);
        key.setDeviceName(deviceName);
        key.setMessageType(KafkaKey.Type.PropertiesUpload);
        key.setProductId(productId);
        deviceOperationMsgSender.sendMessage(key, value);

        DeviceOperationLog deviceOperationLog = new DeviceOperationLog();
        deviceOperationLog.setDeviceName(deviceName);
        deviceOperationLog.setProductId(productId);
        deviceOperationLog.setSetTime(Calendar.getInstance().getTimeInMillis());
        deviceOperationLog.setId(messageId);
        deviceOperationLog.setSetValue(value);
        deviceOperationLogService.addNewLog(deviceOperationLog);
        return messageId;
    }
}
