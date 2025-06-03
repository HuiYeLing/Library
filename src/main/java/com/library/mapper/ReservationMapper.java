package com.library.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper {
    @Insert("INSERT INTO reservation (seat_id, user_id, start_time, status) VALUES (#{seatId}, #{userId}, NOW(), 'ACTIVE')")
    int insertReservation(@Param("seatId") long seatId, @Param("userId") long userId);
}
