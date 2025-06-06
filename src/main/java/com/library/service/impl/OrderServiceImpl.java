package com.library.service.impl;

import com.library.domain.Order;
import com.library.entity.vo.ApiResult;
import com.library.mapper.OrderMapper;
import com.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ApiResult createOrder(Order order) {
        int result = orderMapper.insertOrder(order);
        return result > 0 ? ApiResult.ok("订单创建成功") : ApiResult.error("订单创建失败");
    }

    @Override
    public ApiResult getOrderById(Long id) {
        Order order = orderMapper.selectOrderById(id);
        return order != null ? ApiResult.ok(order) : ApiResult.error("订单不存在");
    }

    @Override
    public ApiResult getOrdersByUserId(Long userId) {
        List<Order> orders = orderMapper.selectOrdersByUserId(userId);
        return ApiResult.ok(orders);
    }

    @Override
    public ApiResult deleteOrder(Long id) {
        int result = orderMapper.deleteOrderById(id);
        return result > 0 ? ApiResult.ok("订单删除成功") : ApiResult.error("订单删除失败");
    }
}