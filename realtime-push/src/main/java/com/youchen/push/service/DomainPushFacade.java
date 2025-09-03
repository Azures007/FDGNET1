package com.youchen.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.List;

public interface DomainPushFacade {

    void pushOrderToday(String base, String line, List<Integer> clazz, Integer orderId, String orderNo, String productName,
                        String estimatedOutput, String unit, String specification, LocalDateTime plannedStartTime, LocalDateTime plannedCompletionTime, Integer orderProcessId);

    void pushOrderTomorrow(String base, String line, List<Integer> clazz, Integer orderId, String orderNo, String productName,
                          String estimatedOutput, String unit, String specification, LocalDateTime plannedStartTime, LocalDateTime plannedCompletionTime, Integer orderProcessId);

    void pushOrderCancelled(String base, String line, List<Integer> clazz, Integer orderId, String orderNo, String productName,
                           String estimatedOutput, String unit, String specification, LocalDateTime plannedStartTime, LocalDateTime plannedCompletionTime, String reason, Integer orderProcessId);

    void pushQcReview(String base, String line, Integer clazz, String docNo, String productName, String checker, LocalDateTime inspectionTime) throws JsonProcessingException;

    void pushQcDaily(String base, String line, Integer clazz, String docNo, String productName, String checker, LocalDateTime inspectionTime) throws JsonProcessingException;
}
