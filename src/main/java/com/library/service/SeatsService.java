package com.library.service;

import com.library.entity.vo.ApiResult;

public interface SeatsService {
    public ApiResult getSeatById(long id);
    public ApiResult getListSeats();
    public ApiResult updateSeatStatus(long id, String status);
    public ApiResult reserveSeat(long id);
    public ApiResult leaveSeat(long id);
}
