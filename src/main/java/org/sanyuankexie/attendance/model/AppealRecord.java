package org.sanyuankexie.attendance.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.sanyuankexie.attendance.common.handler.UserStatusHandler;

import java.util.List;

@Data
@TableName(autoResultMap = true)
public class AppealRecord {
    private String id; // 本次申诉编号
    private String signRecordId; // 关联的本次申诉对应的签到记录
    private User appealUser; // 申诉人
    private String requireAddTime; // 申诉需要补加的时长
    private int status; // 2 -> 申诉审核不通过, 1 -> 申诉审核通过, 0 -> 申诉审核中, -1 -> 申诉提交失败, -2 -> 状态异常
    private List<String> appealImageUrls; // 申诉附带的证明图片地址
    private String reason; // 此次申诉的原因理由
    private User operator; // 此次申诉的处理人
    private long appealTime; // 此次申诉提交的时间戳
    private long dealTime; // 此次申诉的处理时间戳
    private String realAddTime; // 此次申诉实际批准补加的时长
    private String failedReason; // 此次申诉不通过的理由
    private String term; // 此次申诉的学期
}
