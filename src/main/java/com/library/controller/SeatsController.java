package com.library.controller;

import com.library.entity.vo.ApiResult;
import com.library.service.SeatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
public class SeatsController {
    @Autowired
    private SeatsService seatsService;
    // 通过id获取座位信息
    @GetMapping("/getSeatById")
    public ApiResult getSeatById(long id) {
        return seatsService.getSeatById(id);
    }
    @GetMapping("/getListSeats")
    public ApiResult getListSeats() {
        return seatsService.getListSeats();
    }
    @GetMapping("/updateSeatStatus")
    public ApiResult updateSeatStatus(long id, String status) {
        return seatsService.updateSeatStatus(id, status);
    }
    @GetMapping("/reserveSeat")
    public ApiResult reserveSeat(@RequestParam(required = false) Long id) {
        if (id == null) {
            return ApiResult.error("座位ID不能为空");
        }
        return seatsService.reserveSeat(id);
    }
    @GetMapping("/leaveSeat")
    public ApiResult leaveSeat(@RequestParam(required = false) Long id) {
        if (id == null) {
            return ApiResult.error("座位ID不能为空");
        }
        return seatsService.leaveSeat(id);
    }
}
