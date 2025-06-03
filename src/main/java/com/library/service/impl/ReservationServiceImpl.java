package com.library.service.impl;

import com.library.mapper.ReservationMapper;
import com.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public int createReservation(long userId, long seatId) {
        return reservationMapper.insertReservation(seatId, userId);
    }
}