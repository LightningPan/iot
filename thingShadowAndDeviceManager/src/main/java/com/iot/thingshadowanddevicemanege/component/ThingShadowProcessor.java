package com.iot.thingshadowanddevicemanege.component;

import com.google.gson.Gson;
import com.iot.thingshadowanddevicemanege.model.DeviceMetaData;
import com.iot.thingshadowanddevicemanege.model.DeviceProperties;
import com.iot.thingshadowanddevicemanege.model.DeviceStatus;
import com.iot.thingshadowanddevicemanege.model.KafkaKey;
import com.iot.thingshadowanddevicemanege.model.product.Product;
import com.iot.thingshadowanddevicemanege.model.product.ThingModel;
import com.iot.thingshadowanddevicemanege.service.DeviceMetaDataService;
import com.iot.thingshadowanddevicemanege.service.DevicePropertiesService;
import com.iot.thingshadowanddevicemanege.service.DeviceStatusService;
import com.iot.thingshadowanddevicemanege.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

import static com.iot.thingshadowanddevicemanege.model.KafkaKey.Type.PropertiesUpload;
import static com.iot.thingshadowanddevicemanege.model.product.DataType.Type.*;

@Component
public class ThingShadowProcessor {

    private final RedisTemplate<String, String> redisTemplate;
    private final DeviceMetaDataService deviceMetaDataService;
    private final DeviceStatusService deviceStatusService;
    private final ProductService productService;
    private final DevicePropertiesService devicePropertiesService;
    private final DeviceOperationMsgSender deviceOperationMsgSender;

    @Autowired
    public ThingShadowProcessor(RedisTemplate<String, String> redisTemplate, DeviceMetaDataService deviceMetaDataService, DeviceStatusService deviceStatusService, ProductService productService, DevicePropertiesService devicePropertiesService, DeviceOperationMsgSender deviceOperationMsgSender) {
        this.redisTemplate = redisTemplate;
        this.deviceMetaDataService = deviceMetaDataService;
        this.deviceStatusService = deviceStatusService;
        this.productService = productService;
        this.devicePropertiesService = devicePropertiesService;
        this.deviceOperationMsgSender = deviceOperationMsgSender;
    }

    public void processOnlineStatus(Long productId, String deviceName, String value, long time) {
        DeviceMetaData deviceMetaData = deviceMetaDataService.find(deviceName, productId, null, null).get(0);
        if (deviceMetaData == null) return;
        Optional<DeviceStatus> deviceStatusOptional = deviceStatusService.findDeviceStatus(deviceMetaData.getDeviceStatusId());
        DeviceStatus deviceStatus;
        if (deviceStatusOptional.isEmpty()) {
            return;
        } else {
            deviceStatus = deviceStatusOptional.get();
        }
        if (deviceStatus.getLastOnlineStatus() > time) return;
        deviceMetaData.setOnlineStatus(Boolean.parseBoolean(value));

        Optional<DeviceProperties> devicePropertiesOptional = devicePropertiesService.findDeviceProperties(deviceMetaData.getDeviceStatusId());
        DeviceProperties deviceProperties;
        if (devicePropertiesOptional.isPresent()) {
            deviceProperties = devicePropertiesOptional.get();
        } else {
            return;
        }
        if (!deviceProperties.isModifiedAfterOffline()) {
            return;
        }
        deviceProperties.setModifiedAfterOffline(false);
        String messageId = UUID.randomUUID().toString();
        KafkaKey key = new KafkaKey();
        key.setDeviceName(deviceName);
        key.setProductId(productId);
        key.setMessageType(PropertiesUpload);
        key.setMessageId(messageId);
        deviceOperationMsgSender.sendMessage(key, deviceProperties.getPropertyValues());
        deviceProperties.setMessageId(messageId);
        deviceProperties.setSuccess("waiting");
        devicePropertiesService.updateDeviceStatus(deviceProperties);
    }

    public void updateDeviceStatus(Long productId, String deviceName, String value) {
        DeviceMetaData deviceMetaData = deviceMetaDataService.find(deviceName, productId, null, null).get(0);
        if (deviceMetaData == null) return;
        Optional<DeviceStatus> deviceStatusOptional = deviceStatusService.findDeviceStatus(deviceMetaData.getDeviceStatusId());
        Optional<Product> productOptional = productService.findProductById(productId);
        Product product;
        if (productOptional.isEmpty()) {
            return;
        } else {
            product = productOptional.get();
        }
        DeviceStatus deviceStatus;
        if (deviceStatusOptional.isEmpty()) {
            return;
        } else {
            deviceStatus = deviceStatusOptional.get();
        }
        HashMap<String, Object> res = new HashMap<>();
        Map<String, Object> old = new JacksonJsonParser().parseMap(deviceStatus.getStatusValues());
        Map<String, Object> newValue = new JacksonJsonParser().parseMap(value);
        ThingModel[] thingModels = new Gson().fromJson(product.getProperties(), ThingModel[].class);
        for (ThingModel thingModel : thingModels) {
            if (thingModel.getAccessMode() != ThingModel.AccessMode.R) {
                continue;
            }
            processThingModel(res, old, newValue, thingModel, true);
        }
        deviceStatus.setStatusValues(new Gson().toJson(res));
        deviceStatus.setGmtModified(Calendar.getInstance().getTimeInMillis());
        deviceStatusService.updateDeviceStatus(deviceStatus);
    }

    public Map<String, Object> updateDeviceProperties(Long productId,
                                                      String deviceName,
                                                      String messageId,
                                                      String value,
                                                      boolean compareMessageId,
                                                      boolean in) {
        DeviceMetaData deviceMetaData = deviceMetaDataService.find(deviceName, productId, null, null).get(0);
        if (deviceMetaData == null) return null;
        Optional<DeviceProperties> devicePropertiesOptional = devicePropertiesService.findDeviceProperties(deviceMetaData.getDeviceStatusId());
        Optional<Product> productOptional = productService.findProductById(productId);
        Product product;
        if (productOptional.isEmpty()) {
            return null;
        } else {
            product = productOptional.get();
        }
        DeviceProperties deviceProperties;
        if (devicePropertiesOptional.isPresent()) {
            deviceProperties = devicePropertiesOptional.get();
        } else {
            return null;
        }
        HashMap<String, Object> res = new HashMap<>();
        Map<String, Object> old = new JacksonJsonParser().parseMap(deviceProperties.getPropertyValues());
        Map<String, Object> newValue = new JacksonJsonParser().parseMap(value);
        ThingModel[] thingModels = new Gson().fromJson(product.getProperties(), ThingModel[].class);
        for (ThingModel thingModel : thingModels) {
            if (thingModel.getAccessMode() != ThingModel.AccessMode.RW) {
                continue;
            }
            processThingModel(res, old, newValue, thingModel, in);
        }
        deviceProperties.setPropertyValues(new Gson().toJson(res));
        deviceProperties.setGmtModified(Calendar.getInstance().getTimeInMillis());
        if (compareMessageId && deviceProperties.getMessageId().equals(messageId)) {
            deviceProperties.setSuccess("true");
            deviceProperties.setMessageId("");
        }
        if (!deviceMetaData.isOnlineStatus()) {
            deviceProperties.setModifiedAfterOffline(true);
        }
        devicePropertiesService.updateDeviceStatus(deviceProperties);
        return res;
    }

    private void processThingModel(HashMap<String, Object> res, Map<String, Object> old, Map<String, Object> newValue, ThingModel thingModel, boolean in) {
        String key = thingModel.getName();
        Object curVal = null;
        if (newValue.get(key) != null) {
            curVal = newValue.get(key);
        } else if (old.get(key) != null) {
            res.put(key, old.get(key));
        } else {
            return;
        }
        if (thingModel.getDataType().getDataType() == INT) {
            int max = Integer.parseInt(thingModel.getDataType().getSpecs().getMax());
            int min = Integer.parseInt(thingModel.getDataType().getSpecs().getMin());
            int step = Integer.parseInt(thingModel.getDataType().getSpecs().getStep());
            Integer val = (Integer) curVal;
            if (val > max) {
                val = max;
            }
            if (val < min) {
                val = min;
            }
            val = val - (val % step);
            res.put(key, val);
        } else if (thingModel.getDataType().getDataType() == FLOAT) {
            float max = Float.parseFloat(thingModel.getDataType().getSpecs().getMax());
            float min = Float.parseFloat(thingModel.getDataType().getSpecs().getMin());
            String step = thingModel.getDataType().getSpecs().getStep();
            Float val = (Float) curVal;
            if (val > max) {
                val = max;
            }
            if (val < min) {
                val = min;
            }
            DecimalFormat decimalFormat = new DecimalFormat(step);
            res.put(key, Float.parseFloat(decimalFormat.format(val)));
        } else if (thingModel.getDataType().getDataType() == DOUBLE) {
            double max = Double.parseDouble(thingModel.getDataType().getSpecs().getMax());
            double min = Double.parseDouble(thingModel.getDataType().getSpecs().getMin());
            String step = thingModel.getDataType().getSpecs().getStep();
            Double val = (Double) curVal;
            if (val > max) {
                val = max;
            }
            if (val < min) {
                val = min;
            }
            DecimalFormat decimalFormat = new DecimalFormat(step);
            res.put(key, Double.parseDouble(decimalFormat.format(val)));
        } else if (thingModel.getDataType().getDataType() == LONG) {
            long max = Long.parseLong(thingModel.getDataType().getSpecs().getMax());
            long min = Long.parseLong(thingModel.getDataType().getSpecs().getMin());
            long step = Long.parseLong(thingModel.getDataType().getSpecs().getStep());
            Long val = (Long) curVal;
            if (val > max) {
                val = max;
            }
            if (val < min) {
                val = min;
            }
            val = val - (val % step);
            res.put(key, val);
        } else if (thingModel.getDataType().getDataType() == ENUM) {
            if (in) {
                Integer val = (Integer) curVal;
                if (val < 0) return;
                if (val < thingModel.getDataType().getSpecs().getEnums().length) {
                    res.put(key, thingModel.getDataType().getSpecs().getEnums()[val]);
                }
            } else {
                String val = (String) curVal;
                Integer resVal = null;
                for (int i = 0; i < thingModel.getDataType().getSpecs().getEnums().length; i++) {
                    if (val.equals(thingModel.getDataType().getSpecs().getEnums()[i])) {
                        resVal = i;
                        break;
                    }
                }
                res.put(key, resVal);
            }

        } else if (thingModel.getDataType().getDataType() == TEXT) {
            res.put(key, curVal);
        } else if (thingModel.getDataType().getDataType() == ARRAY) {
            Object[] val = (Object[]) curVal;
            if (thingModel.getDataType().getSpecs().getSize() < val.length) {
                return;
            }
            res.put(key, val);
        } else if (thingModel.getDataType().getDataType() == BOOL) {
            res.put(key, curVal);
        } else if (thingModel.getDataType().getDataType() == DATE) {
            res.put(key, curVal);
        } else if (thingModel.getDataType().getDataType() == STRUCT) {
            res.put(key, curVal);
        }
    }

}
