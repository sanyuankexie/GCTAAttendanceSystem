package org.sanyuankexie.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
//import org.apache.ibatis.annotations.Mapper;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.DTO.UserStatusEnum;
import org.sanyuankexie.attendance.model.AttendanceRank;
import org.sanyuankexie.attendance.model.AttendanceRecord;

import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.Constants.MYBATIS_PLUS;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.UNDERSCORE;

@Mapper
public interface AttendanceRecordMapper extends BaseMapper<RecordDTO>     {

    @Insert("INSERT INTO attendance_record " +
            "(id, user_id, start, end, status, operator_id,term,accumulated_time) " +
            "VALUES(#{id},#{userId}, #{start}, #{end}, #{status}, #{operatorId},#{term},#{accumulatedTime})")
    void insert(AttendanceRecord record);


    @Select("SELECT * FROM attendance_record " +
            "WHERE user_id=#{userId} AND status=#{status}")
    AttendanceRecord selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") int status);


    @Update("UPDATE attendance_record SET " +
            "end=#{end}, status=#{status}, operator_id=#{operatorId} " +
            "WHERE id=#{id}")
    void updateById(AttendanceRecord record);

    //手写语句需要映射+继承base
    @ResultMap( MYBATIS_PLUS + UNDERSCORE + "RecordDTO")
    @Select("SELECT " +
            "r.user_id user_id, u.name user_name, u.dept user_dept, u.location user_location " +
            "FROM attendance_record r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE r.status=1")
    List<RecordDTO> selectOnlineRecord();

    //手写语句需要映射+继承base
    @ResultMap( MYBATIS_PLUS + UNDERSCORE + "RecordDTO")
    @Select("SELECT " +
            "r.id id, r.user_id user_id, r.start start, r.end end, r.status status, " +
            "u.name user_name, u.dept user_dept, u.location user_location, r.accumulated_time accumulated_time " +
            "FROM attendance_record r " +
            "LEFT JOIN user u ON u.id=r.user_id " +
            "WHERE r.user_id=#{userId} and r.term=#{term}" +
            "ORDER BY r.start DESC ")
    List<RecordDTO> selectRecordListByUserId(@Param("userId") Long userId,@Param("term") String term);

//    @ResultMap( MYBATIS_PLUS + UNDERSCORE + "RecordDTO")
    @Select("select term from (select term from `attendance_record` where user_id=#{userId} group by term) as art order by term desc")
    List<String> selectTerm(@Param("userId") Long userId);
}
