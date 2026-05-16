package com.example.logistics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String logisticsId;      // 物流编号
    private String fromLocation;     // 起始地点
    private String toLocation;       // 目的地点
    private String status;           // 物流状态(如：运输中、已签收)
    private LocalDateTime eventTime; // 物流节点时间
    private String currentStation;   // 当前所在的物流站点
    private String operator;         // 操作员或设备ID
}