package org.sanyuankexie.attendance.common.DTO;



import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.ibatis.type.JdbcType;
import org.sanyuankexie.attendance.common.handler.UserStatusHandler;

import java.util.Arrays;

@Getter
public enum UserStatusEnum {
    ONLINE(1,"在线"),
    OFFLINE(0,"已签退"),
    FORCE_OFFLINE(-1,"被迫下线"),
    SYSTEM_GIVEN(2,"系统补偿"),
    SYSTEM_TAKEN(3,"系统惩罚"),
    UNKNOWN(-999,"状态未定义");


    @EnumValue
    private final int status;
    @JsonValue
    private final String msg;
    UserStatusEnum(int status, String msg) {
        this.status=status;
        this.msg=msg;
    }
    public static UserStatusEnum getStatus(int status){
        UserStatusEnum[] values = values();
        for (UserStatusEnum value : values) {
         if (value.getStatus()==status)
             return value;
        }
        return UNKNOWN;
    }
}
