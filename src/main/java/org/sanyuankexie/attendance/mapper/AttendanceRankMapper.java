package org.sanyuankexie.attendance.mapper;

import org.apache.ibatis.annotations.*;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.RankExport;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface AttendanceRankMapper {
    @Insert("INSERT INTO attendance_rank " +
            "( user_id, week, total_time,term) " +
            "VALUES( #{userId}, #{week}, #{totalTime},#{term})")
    void insert(AttendanceRank rank);

    @Update("UPDATE attendance_rank SET " +
            "total_time=#{totalTime} " +
            "WHERE id=#{id}")
    void updateById(AttendanceRank rank);

    @Select("SELECT * FROM attendance_rank " +
            "WHERE user_id=#{userId} AND week=#{week} and term=#{term}")
    AttendanceRank selectByUserIdAndWeek(@Param("userId") Long userId, @Param("week") int week,@Param("term") String term);

    @Select("SELECT " +
            "r.user_id, r.total_time, r.week, " +
            "u.name user_name, u.dept user_dept, u.location user_location " +
            "FROM attendance_rank r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE week=#{week} and term=#{term} and ( (r.user_id<#{lessThan} and r.user_id>#{moreThan}  ) or r.user_id in (#{isLeve}) )" +
            "ORDER BY r.total_time DESC " +
            "LIMIT 0, 10")
    List<RankDTO> getTopFive(@Param("week") int week, @Param("moreThan") Long moreThan, @Param("lessThan") Long lessThan,@Param("term") String term,@Param("isLeve") String isLeve);

    @Select("SELECT " +
            "r.user_id, r.total_time, r.week, " +
            "u.name user_name, u.dept user_dept, u.location user_location " +
            "FROM attendance_rank r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE week=#{week} and term=#{term} and ( (r.user_id<#{lessThan} and r.user_id>#{moreThan}  ) and r.user_id not in (#{isLeve}) )" +
            "ORDER BY r.total_time DESC " +
            "LIMIT 0, 10")
    List<RankDTO> getTopOldFive(@Param("week") int week, @Param("moreThan") Long moreThan, @Param("lessThan") Long lessThan,@Param("term") String term,@Param("isLeve") String isLeve);



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
    @Select("SELECT id ,name ,dept, round(total_time/1000/60/60,2) as total_time,week from user as u" +
            " left JOIN (SELECT user_id,term,total_time,week FROM attendance_rank where week=#{week} and term=#{term}) as r on r.user_id=u.id where  u.id > #{lessThan} or u.id in (#{isLeve}) " +
            "ORDER BY total_time DESC")
    List<RankExport> getNewWeekRank(@Param("term") String term, @Param("week") String week, @Param("lessThan")Long lessThan, @Param("isLeve")String isLeve);
    @Select("SELECT id ,name ,dept, round(total_time/1000/60/60,2) as total_time,week from user as u" +
            " left JOIN (SELECT user_id,term,total_time,week FROM attendance_rank where week=#{week} and term=#{term}) as r on r.user_id=u.id where  not (u.id > #{lessThan} or u.id in (#{isLeve})) " +
            "ORDER BY total_time DESC")
    List<RankExport> getOldWeekRank(@Param("term") String term, @Param("week") String week, @Param("lessThan")Long lessThan, @Param("isLeve")String isLeve);
}
