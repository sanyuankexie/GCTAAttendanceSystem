package org.sanyuankexie.attendance.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.sanyuankexie.attendance.model.User;

@Mapper
public interface UserMapper  {

    @Select("SELECT * FROM user WHERE id=#{id}")
    User selectByUserId(Long id);
}
