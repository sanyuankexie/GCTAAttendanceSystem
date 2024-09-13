package org.sanyuankexie.attendance.mapper;

import org.apache.ibatis.annotations.*;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.RankExport;
import org.sanyuankexie.attendance.model.TermRankExport;

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
            "WHERE week=#{week} and term=#{term} and u.grade=#{grade}  " +
            "ORDER BY r.total_time DESC " +
            "LIMIT 0, 10")
    List<RankDTO> getTopFive(@Param("week") int week, @Param("term") String term,@Param("grade") int grade);

    @Select("SELECT " +
            "r.user_id, r.total_time, r.week, " +
            "u.name user_name, u.dept user_dept, u.location user_location " +
            "FROM attendance_rank r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE week=#{week} and term=#{term} and u.grade<#{grade} " +
            "ORDER BY r.total_time DESC " +
            "LIMIT 0, 10")
    List<RankDTO> getTopOldFive(@Param("week") int week,@Param("term") String term,@Param("grade") int grade);



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
    @Select("UPDATE attendance_rank SET total_time=total_time-#{time} WHERE user_id=#{userId} and week=#{week}")
    RankDTO sub(@Param("userId") Long userId,
                @Param("week") Integer week,
                @Param("time") Long time);
    @Select("UPDATE attendance_rank SET total_time=#{time} WHERE user_id=#{userId} and week=#{week}")
    RankDTO set(@Param("userId") Long userId,
                @Param("week") Integer week,
                @Param("time") Long time);

    @Select("SELECT id ,name ,dept, round(total_time/1000/60/60,2) as total_time,week from user as u" +
            " left JOIN (SELECT user_id,term,total_time,week FROM attendance_rank where week=#{week} and term=#{term}) as r on r.user_id=u.id where  u.grade=#{grade} " +
            "ORDER BY total_time DESC")
    List<RankExport> getNewWeekRank(@Param("term") String term, @Param("week") String week, @Param("grade")int grade);

    @Select("SELECT u.id, u.name, u.dept, " +
            "ROUND(SUM(r.total_time) / 1000 / 60 / 60, 2) AS total_time, " +  // 总时间
            "COUNT(DISTINCT r.week) AS total_weeks, " +  // 统计week数量
            "ROUND(SUM(r.total_time) / COUNT(DISTINCT r.week) / 1000 / 60 / 60, 2) AS avg_time_per_week, " +  // 计算每周平均时间
            "ROUND(MAX(r.total_time) / 1000 / 60 / 60, 2) AS max_time_per_week, " +  // 每周最大时间
            "ROUND(MIN(r.total_time) / 1000 / 60 / 60, 2) AS min_time_per_week, " +  // 每周最小时间
            "r.week " +
            "FROM user AS u " +
            "LEFT JOIN attendance_rank AS r ON r.user_id = u.id " +
            "WHERE r.term = #{term} AND u.grade = #{grade} " +  // 查询指定学期和年级
            "AND r.week BETWEEN #{startWeek} AND #{endWeek} " +  // 加入起始周和结束周
            "GROUP BY u.id, u.name, u.dept " +  // 按用户分组
            "ORDER BY total_time DESC")
    List<TermRankExport> getTermRankWithWeeklyStats(
            @Param("term") String term,
            @Param("grade") int grade,
            @Param("startWeek") int startWeek,
            @Param("endWeek") int endWeek);

    @Select("SELECT id ,name ,dept, round(total_time/1000/60/60,2) as total_time,week from user as u" +
            " left JOIN (SELECT user_id,term,total_time,week FROM attendance_rank where week=#{week} and term=#{term}) as r on r.user_id=u.id where  u.grade<#{grade} " +
            "ORDER BY total_time DESC")
    List<RankExport> getOldWeekRank(@Param("term") String term, @Param("week") String week,@Param("grade")int grade);



    //新生学期总时常
    @Select("SELECT id ,name ,dept, round(sum(total_time)/1000/60,2) as total_time from user as u" +
            " left JOIN (SELECT user_id,term,total_time FROM attendance_rank where term=#{term}) as r on r.user_id=u.id where  u.grade=#{grade} " +
            "  group by id ORDER BY total_time DESC")
    List<RankExport> getAllNewWeekRanMine(@Param("term") String term, @Param("grade")int grade);
}
