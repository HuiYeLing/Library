package com.library.service.impl;

import com.library.domain.Seats;
import com.library.entity.vo.ApiResult;
import com.library.mapper.SeatsMapper;
import com.library.service.SeatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatsServiceImpl implements SeatsService {
    @Autowired
    private SeatsMapper seatsMapper;
    // 通过id获取座位信息
    public ApiResult getSeatById(long id) {
        ApiResult r = ApiResult.error("获取座位失败");
        Seats seat = seatsMapper.getSeatById(id);
        if (seat != null) {
            r = ApiResult.ok(seat);
        } else {
            r = ApiResult.error("座位不存在");
        }
        return r;
    }
    // 获取所有座位信息
    public ApiResult getListSeats() {
        ApiResult r = ApiResult.error("获取所有座位信息失败");
        List<Seats> seats = seatsMapper.getListSeats();
        if (seats != null && !seats.isEmpty()) {
            r = ApiResult.ok(seats);
        } else {
            r = ApiResult.error("没有座位信息");
        }
        return r;
    }
    // 更新座位状态
    public ApiResult updateSeatStatus(long id, String status) {
        ApiResult r = ApiResult.error("更新座位状态失败");
        int result = seatsMapper.updateSeatStatus(id, status);
        if (result > 0) {
            r = ApiResult.ok("更新座位状态成功");
        } else {
            r = ApiResult.error("更新座位状态失败");
        }
        return r;
    }
    // 预定座位
    @Override
    public ApiResult reserveSeat(long id) {
        // 先查询座位是否存在
        Seats seat = seatsMapper.getSeatById(id);

        if (seat == null) {
            return ApiResult.error("座位不存在");
        }

        // 检查座位状态是否为可用
        if (!Seats.STATUS_AVAILABLE.equals(seat.getStatus())) {
            return ApiResult.error("座位不可用，当前状态：" + seat.getStatus());
        }

        // 更新座位状态为已占用
        int result = seatsMapper.updateSeatStatus(id, Seats.STATUS_OCCUPIED);

        if (result > 0) {
            // 获取更新后的座位信息
            seat = seatsMapper.getSeatById(id);
            return ApiResult.ok(seat, "预定座位成功");
        } else {
            return ApiResult.error("预定座位失败，请稍后再试");
        }
    }
    // 离开座位
    @Override
    public ApiResult leaveSeat(long id) {
        // 先查询座位是否存在
        Seats seat = seatsMapper.getSeatById(id);

        if (seat == null) {
            return ApiResult.error("座位不存在");
        }

        // 检查座位状态是否为已占用
        if (!Seats.STATUS_OCCUPIED.equals(seat.getStatus())) {
            return ApiResult.error("座位未被占用，当前状态：" + seat.getStatus());
        }

        // 更新座位状态为可用
        int result = seatsMapper.updateSeatStatus(id, Seats.STATUS_AVAILABLE);

        if (result > 0) {
            // 获取更新后的座位信息
            seat = seatsMapper.getSeatById(id);
            return ApiResult.ok(seat, "已离开座位");
        } else {
            return ApiResult.error("离开座位失败，请稍后再试");
        }
    }


}
