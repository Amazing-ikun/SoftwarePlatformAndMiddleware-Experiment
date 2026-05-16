package com.example.logistics.consumer;

import com.example.logistics.model.LogisticsMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class LogisticsConsumer {

    @JmsListener(destination = "WangJingyao.LogisticsTopic",
            containerFactory = "jmsListenerContainerFactory")
    public void receiveLogisticsMessage(LogisticsMessage message) {
        System.out.println("收到物流消息:");
        System.out.println("   物流编号: " + message.getLogisticsId());
        System.out.println("   起始地点: " + message.getFromLocation() + " → " + message.getToLocation());
        System.out.println("   当前站点: " + message.getCurrentStation());
        System.out.println("   物流状态: " + message.getStatus());
        System.out.println("   事件时间: " + message.getEventTime());
        System.out.println("   操作员/设备: " + message.getOperator());
        System.out.println("-----------------------------------");
    }
}