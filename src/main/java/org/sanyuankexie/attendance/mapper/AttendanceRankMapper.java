package org.sanyuankexie.attendance.mapper;

import org.apache.ibatis.annotations.*;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.model.AttendanceRank;

import java.util.List;

@Mapper
public interface AttendanceRankMapper {
    @Insert("INSERT INTO attendance_rank " +
            "(id, user_id, week, total_time) " +
            "VALUES(#{id}, #{userId}, #{week}, #{totalTime})")
    void insert(AttendanceRank rank);

    @Update("UPDATE attendance_rank SET " +
            "total_time=#{totalTime} " +
            "WHERE id=#{id}")
    void updateById(AttendanceRank rank);

    @Select("SELECT * FROM attendance_rank " +
            "WHERE user_id=#{userId} AND week=#{week}")
    AttendanceRank selectByUserIdAndWeek(@Param("userId") Long userId, @Param("week") int week);

    @Select("SELECT " +
            "r.user_id, r.total_time, r.week, " +
            "u.name user_name, u.dept user_dept, u.location user_location " +
            "FROM attendance_rank r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE week=#{week} and r.user_id<#{lessThan} and r.user_id>#{moreThan} " +
            "ORDER BY r.total_time DESC " +
            "LIMIT 0, 10")
    List<RankDTO> getTopFive(@Param("week") int week, @Param("moreThan") Long moreThan, @Param("lessThan") Long lessThan);

    @Select("SELECT " +
            "r.user_id, r.total_time, r.week, " +
            "u.name user_name, u.dept user_dept, u.location user_location " +
            "FROM attendance_rank r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE week=#{week} " +
            "ORDER BY r.total_time DESC ")
    List<RankDTO> getAll(@Param("week") int week);

    @Select("UPDATE attendance_rank SET total_time=total_time+#{time} WHERE user_id=#{userId} and week=#{week}")
    RankDTO add(@Param("userId") Long userId,
                @Param("week") Integer week,
                @Param("time") Long time);
}
