package com.library.controller;

import com.library.domain.Order;
import com.library.entity.vo.ApiResult;
import com.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 创建订单
    @PostMapping("/create")
    public ApiResult createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // 根据订单ID查询
    @GetMapping("/getById")
    public ApiResult getOrderById(@RequestParam Long id) {
        return orderService.getOrderById(id);
    }

    // 根据用户ID查询订单列表
    @GetMapping("/getByUserId")
    public ApiResult getOrdersByUserId(@RequestParam Long userId) {
        return orderService.getOrdersByUserId(userId);
    }

    // 删除订单
    @DeleteMapping("/delete")
    public ApiResult deleteOrder(@RequestParam Long id) {
        return orderService.deleteOrder(id);
    }
}