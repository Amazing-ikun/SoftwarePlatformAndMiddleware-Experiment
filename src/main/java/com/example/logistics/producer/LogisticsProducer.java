package com.example.logistics.producer;

import com.example.logistics.model.LogisticsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import jakarta.jms.Topic;

@Component
public class LogisticsProducer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Topic logisticsTopic;

    public void sendLogisticsMessage(LogisticsMessage message) {
        jmsMessagingTemplate.convertAndSend(logisticsTopic, message);
        System.out.println("已发送物流消息到 Topic: " + message);
    }
}