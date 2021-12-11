package org.sanyuankexie.attendance.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRank {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private Integer week;
    private Long totalTime;
    private String term;
}
