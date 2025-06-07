package com.library.service;

import com.library.domain.Order;
import com.library.entity.vo.ApiResult;

public interface OrderService {
    ApiResult createOrder(Order order);
    ApiResult getOrderById(Long id);
    ApiResult getOrdersByUserId(Long userId);
    ApiResult deleteOrder(Long id);
    ApiResult getAllOrders();
    ApiResult updateOrderStatus(Long orderId, Integer status);
}