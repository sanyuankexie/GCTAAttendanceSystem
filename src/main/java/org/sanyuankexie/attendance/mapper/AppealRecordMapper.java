package org.sanyuankexie.attendance.mapper;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.annotations.*;
import org.sanyuankexie.attendance.common.DTO.AppealQueryDTO;
import org.sanyuankexie.attendance.model.AppealRecord;

import java.util.List;

@Mapper
public interface AppealRecordMapper {

    @Insert("INSERT INTO appeal_record " +
            "(id, sign_record_id, appeal_user_id, require_add_time, status, appeal_image_urls, " +
            "reason, operator_id, appeal_time, deal_time, real_add_time, failed_reason, term) " +
            "VALUES (#{id}, #{signRecordId}, #{appealUser.id}, #{requireAddTime}, #{status}, " +
            "#{appealImageUrls, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, " +
            "#{reason}, #{operator.id}, #{appealTime}, #{dealTime}, #{realAddTime}, #{failedReason}, #{term})")
    void insert(AppealRecord record);

    @Select("SELECT * FROM appeal_record WHERE id = #{id}")
    @Results(id = "appealRecordSingle", value = {
            @Result(property = "appealUser", column = "appeal_user_id",
                    one = @One(select = "org.sanyuankexie.attendance.mapper.UserMapper.selectByUserId")),
            @Result(property = "operator", column = "operator_id",
                    one = @One(select = "org.sanyuankexie.attendance.mapper.UserMapper.selectByUserId")),
            @Result(property = "appealImageUrls", column = "appeal_image_urls",
                    typeHandler = JacksonTypeHandler.class)
    })
    AppealRecord selectById(@Param("id") String id);

    @Select("<script>" +
            "SELECT * FROM appeal_record ar " +
            "JOIN user u ON ar.appeal_user_id = u.id " +
            "WHERE 1=1 " +
            "<if test='appealId != null and appealId != \"\"'>AND ar.id = #{appealId} </if>" +
            "<if test='status != null'>AND ar.status = #{status} </if>" +
            "<if test='term != null and term != \"\"'>AND ar.term = #{term} </if>" +
            "<if test='studentId != null'>AND u.id = #{studentId} </if>" +
            "<if test='name != null and name != \"\"'>AND u.name = #{name} </if>" +
            "<if test='department != null and department != \"\"'>AND u.dept = #{department} </if>" +
            "<if test='operator != null'>AND ar.operator_id = #{operator} </if>" +
            "</script>")
    @Results(id = "appealRecordResultMap", value = {
            @Result(property = "appealUser", column = "appeal_user_id", one = @One(select = "org.sanyuankexie.attendance.mapper.UserMapper.selectByUserId")),
            @Result(property = "operator", column = "operator_id", one = @One(select = "org.sanyuankexie.attendance.mapper.UserMapper.selectByUserId")),
            @Result(property = "appealImageUrls", column = "appeal_image_urls", typeHandler = JacksonTypeHandler.class)
    })
    List<AppealRecord> selectAppealRecords(
            @Param("appealId") String appealId,
            @Param("name") String name,
            @Param("department") String department,
            @Param("term") String term,
            @Param("studentId") Long studentId,
            @Param("status") Integer status,
            @Param("operator") Long operator
    );


    @Update("UPDATE appeal_record SET " +
            "status = #{status}, operator_id = #{operator.id}, deal_time = #{dealTime}, " +
            "real_add_time = #{realAddTime}, failed_reason = #{failedReason} " +
            "WHERE id = #{id}")
    void updateById(AppealRecord record);

}
