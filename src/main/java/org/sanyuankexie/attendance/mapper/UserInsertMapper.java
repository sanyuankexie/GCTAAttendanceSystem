package org.sanyuankexie.attendance.mapper;



//import org.mybatis.spring.annotation.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.sanyuankexie.attendance.model.User;
//import org.sanyuankexie.attendance.entry.User;

@Mapper
public interface UserInsertMapper extends BaseMapper<User> {
}
