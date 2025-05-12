package com.library.mapper;

import com.library.domain.Seats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeatsMapper {
    // 根据ID查询座位
    @Select("SELECT * FROM seats WHERE id = #{id}")
    Seats getSeatById(long id);
    // 查询所有座位
    @Select("SELECT * FROM seats")
    List<Seats> getListSeats();
    // 更新座位状态
    @Update("UPDATE seats SET status = #{status} WHERE id = #{id}")
    int updateSeatStatus(@Param("id") long id, @Param("status") String status);
    // 预约座位
    @Update("UPDATE seats SET status = 'reserved' WHERE id = #{id} AND status = 'available'")
    int reserveSeat(long id);
    // 离开座位
    @Update("UPDATE seats SET status = 'available' WHERE id = #{id} AND status = 'reserved'")
    int leaveSeat(@Param("id") long id);

}
