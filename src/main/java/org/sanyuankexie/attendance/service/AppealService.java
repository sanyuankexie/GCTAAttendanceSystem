package org.sanyuankexie.attendance.service;


import org.sanyuankexie.attendance.common.DTO.AppealDealDTO;
import org.sanyuankexie.attendance.common.DTO.AppealQueryDTO;
import org.sanyuankexie.attendance.common.DTO.RankDTO;
import org.sanyuankexie.attendance.mapper.AppealRecordMapper;
import org.sanyuankexie.attendance.model.AppealRecord;
import org.sanyuankexie.attendance.model.AppealRequest;
import org.sanyuankexie.attendance.model.SystemInfo;
import org.sanyuankexie.attendance.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AppealService {

    @Resource
    private SystemInfo systemInfo;

    @Resource
    private AppealRecordMapper appealRecordMapper;

    @Resource
    private UserService userService;

    public AppealService(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }


    @Transactional
    public Object uploadAppeal(AppealRequest appealRequest) {
        AppealRecord appealRecord = new AppealRecord();
        long nowTime = System.currentTimeMillis();
        String thisRecordId = Long.toString(nowTime) + appealRequest.getAppealUser().getId();
        appealRecord.setId(thisRecordId);
        appealRecord.setSignRecordId(appealRequest.getSignRecordId());
        appealRecord.setAppealUser(appealRequest.getAppealUser());
        appealRecord.setRequireAddTime(appealRequest.getRequireAddTime());
        appealRecord.setReason(appealRequest.getReason());
        appealRecord.setAppealImageUrls(appealRequest.getAppealImageUrls());
        appealRecord.setAppealTime(nowTime);
        appealRecord.setStatus(0);
        appealRecord.setTerm(systemInfo.getTerm());
        System.out.print(appealRecord.toString());
        appealRecordMapper.insert(appealRecord);
        return thisRecordId;
    }

    /**
     * 获取申诉记录列表
     *
     * @param appealQueryDTO 查询条件
     * @return 申诉记录列表
     */
    public List<AppealRecord> getAppealList(AppealQueryDTO appealQueryDTO) {
        return appealRecordMapper.selectAppealRecords(
                appealQueryDTO.getAppealId(),
                appealQueryDTO.getName(),
                appealQueryDTO.getDepartment(),
                appealQueryDTO.getTerm(),
                appealQueryDTO.getStudentId(),
                appealQueryDTO.getStatus(),
                appealQueryDTO.getOperator()
        );
    }

    @Transactional
    public String dealAppeal(AppealDealDTO appealDealDTO) {
        // 查询当前申诉记录
        AppealRecord record = appealRecordMapper.selectById(appealDealDTO.getDealAppealId());
        if (record == null) {
            throw new IllegalArgumentException("未找到对应的申诉记录");
        }
        // 获取处理人信息
        User operator = new User();
        operator.setId(appealDealDTO.getOperatorId());
        // 更新记录
        record.setOperator(operator); // 处理人对象，只传 ID 即可
        record.setDealTime(System.currentTimeMillis()); // 处理时间
        record.setRealAddTime(appealDealDTO.getRealAddTime());
        // 执行增加时长
        if (appealDealDTO.getResult()) {
            record.setStatus(1); // 设置处理状态（如1=通过，2=驳回）
            userService.modifyTime(
                    "add",
                    record.getAppealUser().getId(),
                    appealDealDTO.getRealAddTime(),
                    systemInfo.getPassword(),
                    null
            );
        } else {
            record.setStatus(2);
            record.setFailedReason(appealDealDTO.getFailedReason());
        }
        appealRecordMapper.updateById(record); // 更新数据库

        return "处理成功";
    }

}
