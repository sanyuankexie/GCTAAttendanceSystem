package org.sanyuankexie.attendance.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.sanyuankexie.attendance.common.handler.UserStatusHandler;

@Data
@AllArgsConstructor
@TableName(autoResultMap = true)
public class AttendanceRecord {
    private String id;
    private Long userId;
    private Long start;
    private Long end;
    @TableField(jdbcType = JdbcType.INTEGER,typeHandler = UserStatusHandler.class)
    private int status; // 1 -> Online, 0 -> Offline, -1 -> Be reported 2 System given
    private Long operatorId;
    private String term;
    private Object accumulatedTime;
}
