package org.sanyuankexie.attendance.common.DTO;

import lombok.Data;

@Data
public class UserStatusDTO {
    private Long userId;
    private String userName;
    private Integer status;
    private Integer week;
}
