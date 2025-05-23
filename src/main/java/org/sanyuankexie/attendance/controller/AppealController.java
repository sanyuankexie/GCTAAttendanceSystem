package org.sanyuankexie.attendance.controller;

import org.sanyuankexie.attendance.common.DTO.AppealDealDTO;
import org.sanyuankexie.attendance.common.DTO.AppealQueryDTO;
import org.sanyuankexie.attendance.common.api.ResultVO;
import org.sanyuankexie.attendance.common.aspect.annotation.ConvertTime;
import org.sanyuankexie.attendance.common.helper.PageResultHelper;
import org.sanyuankexie.attendance.common.helper.ResultHelper;
import org.sanyuankexie.attendance.model.AppealRecord;
import org.sanyuankexie.attendance.model.AppealRequest;
import org.sanyuankexie.attendance.service.AppealService;
import org.sanyuankexie.attendance.service.MinioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/appeal")
public class AppealController {

    @Resource
    private AppealService appealService;
    @Resource
    private MinioService minioService;

    @ConvertTime
    @PostMapping(value = "/uploadImage")
    public ResultVO<List<String>> uploadImage(@RequestParam("images") MultipartFile[] images) throws Exception {
        return ResultHelper.success(minioService.uploadImages(images),"上传成功");
    }

    @ConvertTime
    @PostMapping(value = "/uploadAppeal")
    public Object uploadAppeal(@Valid @RequestBody AppealRequest appealInfo) {
        return ResultHelper.success(appealService.uploadAppeal(appealInfo).toString(),"上传成功");
    }

    @ConvertTime
    @GetMapping(value = "/getAppeal")
    public ResultVO<PageResultHelper<AppealRecord>> getAppeal(@ModelAttribute @Valid AppealQueryDTO dto) {
        return ResultHelper.success(appealService.getAppealList(dto),"获取成功");
    }

    @ConvertTime
    @PostMapping(value = "/dealAppeal")
    public ResultVO<String> dealAppeal(@Valid @RequestBody AppealDealDTO appealDealDTO) {
        return ResultHelper.success(appealService.dealAppeal(appealDealDTO),"处理完成");
    }

}
