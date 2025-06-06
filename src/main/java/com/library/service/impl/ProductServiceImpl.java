package com.library.service.impl;

import com.library.domain.Order;
import com.library.domain.Product;
import com.library.domain.User;
import com.library.entity.vo.ApiResult;
import com.library.mapper.OrderMapper;
import com.library.mapper.ProductMapper;
import com.library.mapper.UserMapper;
import com.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public ApiResult getProductById(Long id) {
        Product product = productMapper.getProductById(id);
        if (product != null) {
            return ApiResult.ok(product);
        }
        return ApiResult.error("未找到该商品");
    }

    @Override
    public ApiResult getAllProducts() {
        List<Product> list = productMapper.getAllProducts();
        return ApiResult.ok(list);
    }

    @Override
    public ApiResult addProduct(Product product) {
        int res = productMapper.insertProduct(product);
        if (res > 0) {
            return ApiResult.ok("添加成功");
        }
        return ApiResult.error("添加失败");
    }

    @Override
    public ApiResult updateProduct(Product product) {
        int res = productMapper.updateProduct(product);
        if (res > 0) {
            return ApiResult.ok("更新成功");
        }
        return ApiResult.error("更新失败");
    }

    @Override
    public ApiResult deleteProduct(Long id) {
        int res = productMapper.deleteProduct(id);
        if (res > 0) {
            return ApiResult.ok("删除成功");
        }
        return ApiResult.error("删除失败");
    }


    @Override
    public ApiResult buy(Long userId, Long productId) {
        Product product = productMapper.getProductById(productId);
        if (product == null) {
            return ApiResult.error("商品不存在");
        }
        if (product.getStock() == null || product.getStock() < 1) {
            return ApiResult.error("库存不足");
        }
        int updateCount = productMapper.decreaseStock(productId, 1);
        if (updateCount <= 0) {
            return ApiResult.error("扣减库存失败");
        }
        // 查询用户信息
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return ApiResult.error("用户不存在");
        }
        // 组装订单信息
        Order order = new Order();
        order.setUserId(userId);
        order.setUsername(user.getUsername()); // 正确设置用户名
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setTotalPrice(BigDecimal.valueOf(product.getPrice()));
        order.setCreateTime(new java.util.Date());
        int res = orderMapper.insertOrder(order);
        if (res > 0) {
            return ApiResult.ok("购买成功");
        }
        return ApiResult.error("订单创建失败");
    }
}