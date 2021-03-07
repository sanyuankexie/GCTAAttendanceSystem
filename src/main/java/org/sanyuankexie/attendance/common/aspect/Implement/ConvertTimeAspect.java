package org.sanyuankexie.attendance.common.aspect.Implement;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.common.DTO.RecordDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.helper.ClassHelper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


@Component
@Aspect
@Order(1)
public class ConvertTimeAspect {
    DecimalFormat dft = new DecimalFormat("0.00");

    @Pointcut("@annotation(org.sanyuankexie.attendance.common.aspect.annotation.ConvertTime)")
    public void thePointCut() {
    }

    @AfterReturning(pointcut = "thePointCut()", returning = "res")
    public void doAfterReturning(JoinPoint joinPoint, Object res) throws NoSuchFieldException {
        ResultVO<Object> resultVO = (ResultVO<Object>) res;
        Object data = resultVO.getData();
        if (data == null) return;
        if (data instanceof RankDTO) {
            Field[] fields = {
                    ClassHelper.getObjectField(data, "totalTime"),
                    ClassHelper.getObjectField(data, "accumulatedTime"),
            };
            modifiedTimestampToHours(fields, data);
        } else if (data instanceof RecordDTO) {
            Field[] fields = {
                    ClassHelper.getObjectField(data, "start"),
                    ClassHelper.getObjectField(data, "end"),
            };
            modifiedTimestampToDate(fields, data);

        } else if (data instanceof ArrayList) {
            ArrayList dataList = (ArrayList) data;
            if (dataList.size() > 0 && dataList.get(0) instanceof RankDTO) {
                dataList.forEach(
                        it -> {
                            try {
                                Field totalTimeField = ClassHelper.getObjectField(it, "totalTime");
                                totalTimeField.setAccessible(true);
                                totalTimeField.set(it, dft.format(((long) totalTimeField.get(it)) / 1000 * 1.0 / 3600));
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        }
    }

    private void modifiedTimestampToHours(Field[] fields, Object target) {
        Arrays.stream(fields).forEach(
                it -> {
                    try {
                        it.setAccessible(true);
                        if (it.get(target) != null) {
                            it.set(target, dft.format(((long) it.get(target)) / 1000 * 1.0 / 3600));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void modifiedTimestampToDate(Field[] fields, Object target) {
        Arrays.stream(fields).forEach(
                it -> {
                    try {
                        it.setAccessible(true);
                        if (it.get(target) != null) {
                            it.set(target, getTime((long) it.get(target)));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public String getTime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
    }
}