package org.sanyuankexie.attendance.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AppealRequest {
    private String signRecordId; // 关联的本次申诉对应的签到记录
    @Valid
    @NotNull(message = "申诉人不能为空")
    private User appealUser; // 申诉人
    @NotNull(message = "需要补加的时长不能为空")
    private String requireAddTime; // 申诉需要补加的时长
    private List<String> appealImageUrls; // 申诉附带的证明图片地址
    @NotNull(message = "申诉理由不能为空")
    private String reason; // 此次申诉的原因理由
}
