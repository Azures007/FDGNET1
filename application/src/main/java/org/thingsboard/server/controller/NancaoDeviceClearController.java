package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.utils.MyMqttClient;
import org.thingsboard.server.utils.MyMqttRecieveMessage;

@Api(value = "南潮设备计数清零", tags = "南潮设备计数清零")
@RequestMapping("/api/deviceClear")
@RestController
public class NancaoDeviceClearController {



    @ApiOperation("计数清零")
    @GetMapping("/clearDevice")
    public ResponseResult clearDevice(@RequestParam("sn") String sn) {
//        tSysClassService.deleteTSysClass(classId);
        String msg = "{\n" +
                "reqId: \"123\",\n" +
                "method: \"property.reset\"\n" +
                "}";
        String topic ="/sys/"+sn+"/rrpc/request";
        MyMqttClient mqttClient = new MyMqttClient();
        mqttClient.publishMessage(topic,msg,1);

        String responseTopic = "/sys/"+sn+"/rrpc/response";
        MyMqttRecieveMessage myMqttRecieveMessage = new MyMqttRecieveMessage();
        myMqttRecieveMessage.recieve(responseTopic);



        return ResultUtil.success();
    }
}
