package org.sanyuankexie.attendance.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.sanyuankexie.attendance.model.User;

import java.util.List;

@Mapper
public interface UserMapper  {

    @Select("SELECT * FROM user WHERE id=#{id}")
    User selectByUserId(Long id);

    @Select("SELECT * FROM user")
    List<User> selectList();

}
