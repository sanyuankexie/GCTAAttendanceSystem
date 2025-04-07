package org.sanyuankexie.attendance.common.DTO;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AppealDealDTO {
    @NotNull(message = "处理人不能为空")
    private Long operatorId; // 处理人ID
    @NotNull(message = "处理的申诉编号不能为空")
    private String dealAppealId; // 此次处理的申诉编号ID
    @NotNull(message = "处理结果不能为空")
    private Boolean result; // 处理结果
    private String failedReason; // 如果不通过的话，本次不通过原因
    private String realAddTime; // 实际应该补加的时长，若为空则是默认同意为申请者的申请时长
}
