package com.library.mapper;

import com.library.domain.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order` (user_id, username, product_id, product_name, total_price, create_time) " +
            "VALUES (#{userId}, #{username}, #{productId}, #{productName}, #{totalPrice}, #{createTime})")
    int insertOrder(Order order);

    @Select("SELECT * FROM `order` WHERE `id` = #{id}")
    Order selectOrderById(@Param("id") Long id);

    @Select("SELECT * FROM `order` WHERE `user_id` = #{userId}")
    List<Order> selectOrdersByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM `order` WHERE `id` = #{id}")
    int deleteOrderById(@Param("id") Long id);
}