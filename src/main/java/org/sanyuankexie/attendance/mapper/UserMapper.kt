package org.sanyuankexie.attendance.mapper

import org.apache.ibatis.annotations.Delete
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

    @Select("""
        SELECT * FROM USER WHERE role=#{department}
    """)
    fun selectDepartmentManager(@Param("department") department: Int) : List<User>

    @Delete("""
        <script>
        DELETE FROM user 
        WHERE id IN 
        <foreach item="id" collection="idList" open="(" separator="," close=")">
            #{id}
        </foreach>
        </script>
    """)
    fun delete(@Param("idList") idList: List<Long>): Int
}
