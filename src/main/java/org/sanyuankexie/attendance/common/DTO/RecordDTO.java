package org.sanyuankexie.attendance.common.DTO;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.sanyuankexie.attendance.common.handler.UserStatusHandler;

@Data

@TableName( autoResultMap = true ) //开启处理器
public class RecordDTO  {

    private String id;
    private Long userId;
    private String userName;
    private String userDept;
    private String userLocation;
    private Object start;
    private Object end;
    @TableField(jdbcType = JdbcType.INTEGER,typeHandler = UserStatusHandler.class)
    private UserStatusEnum status; // 1 -> Online, 0 -> Offline, -1 -> Be reported
    private Object accumulatedTime;

}
