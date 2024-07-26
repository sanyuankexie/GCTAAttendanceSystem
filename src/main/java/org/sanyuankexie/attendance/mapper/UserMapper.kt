package org.sanyuankexie.attendance.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.sanyuankexie.attendance.model.User

@Mapper
interface UserMapper {

    @Select("""
        SELECT * FROM user WHERE id=#{id}
    """)
    fun selectByUserId(@Param("id") id: Long): User?

    @Select("""
        <script>
        SELECT * FROM user 
        <where>
            <if test='grade != null'>grade = #{grade}</if>
        </where>
        </script>
    """)
    fun selectList(@Param("grade") grade: String?): List<User>
}
