package org.sanyuankexie.attendance.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> implements Serializable {
    private T data;
    private Integer code;
    private String msg;

    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
