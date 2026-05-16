package com.example.logistics.controller;

import com.example.logistics.model.LogisticsMessage;
import com.example.logistics.producer.LogisticsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LogisticsProducer producer;

    @GetMapping("/send")
    public String sendTestMessage() {
        LogisticsMessage message = new LogisticsMessage();
        message.setLogisticsId(UUID.randomUUID().toString());
        message.setFromLocation("北京转运中心");
        message.setToLocation("上海分拣中心");
        message.setStatus("运输中");
        message.setEventTime(LocalDateTime.now());
        message.setCurrentStation("济南中转站");
        message.setOperator("设备ID: SCANNER_001");

        producer.sendLogisticsMessage(message);
        return "消息已发送！物流编号：" + message.getLogisticsId();
    }
}