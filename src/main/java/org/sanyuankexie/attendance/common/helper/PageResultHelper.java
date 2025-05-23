package org.sanyuankexie.attendance.common.helper;

import lombok.Data;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.T;
import org.sanyuankexie.attendance.model.AppealRecord;

import java.util.List;

@Data
public class PageResultHelper<T> {
    private List<T> records;
    private long total;
    private long currentPage;
    private long pageSize;

    public PageResultHelper(List<T> records, long total, long currentPage, long pageSize) {
        this.records = records;
        this.total = total;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

}
