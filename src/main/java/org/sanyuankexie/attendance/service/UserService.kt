package org.sanyuankexie.attendance.service

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.read.listener.PageReadListener
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.opencsv.CSVWriter
import org.sanyuankexie.attendance.common.DTO.RankDTO
import org.sanyuankexie.attendance.common.DTO.UserStatusEnum
import org.sanyuankexie.attendance.common.exception.CExceptionEnum
import org.sanyuankexie.attendance.common.exception.ServiceException
import org.sanyuankexie.attendance.common.helper.TimeHelper
import org.sanyuankexie.attendance.mapper.AttendanceRankMapper
import org.sanyuankexie.attendance.mapper.AttendanceRecordMapper
import org.sanyuankexie.attendance.mapper.UserInsertMapper
import org.sanyuankexie.attendance.mapper.UserMapper
import org.sanyuankexie.attendance.model.AttendanceRank
import org.sanyuankexie.attendance.model.AttendanceRecord
import org.sanyuankexie.attendance.model.SystemInfo
import org.sanyuankexie.attendance.model.User
import org.sanyuankexie.attendance.thread.EmailThread
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.PrintWriter
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

@Service
open class UserService(
    private val timeHelper: TimeHelper,
    private val systemInfo: SystemInfo
) {
    companion object {
        private val defenderMap = ConcurrentHashMap<Long, Long>()
    }

    @Resource
    lateinit var threadPoolTaskExecutor: ThreadPoolTaskExecutor

    @Resource
    lateinit var mailService: MailService

    @Resource
    lateinit var rankMapper: AttendanceRankMapper

    @Resource
    lateinit var rankService: AttendanceRankService

    @Resource
    lateinit var recordService: AttendanceRecordService

    @Autowired
    lateinit var recordMapper: AttendanceRecordMapper

    @Resource
    lateinit var userMapper: UserMapper

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var insertMapper: UserInsertMapper

    @Transactional
    open fun signIn(userId: Long): RankDTO {
        val now = System.currentTimeMillis()
        if (timeHelper.noAllSign(now)) {
            throw ServiceException(CExceptionEnum.No_ALLOW_TIME, userId)
        }
        if (timeHelper.noStart(now)) {
            throw ServiceException(CExceptionEnum.TERM_NO_START, userId)
        }
        val user = getUserByUserId(userId)
            ?: throw ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId)
        val onlineRecord = recordService.getOnlineRecordByUserId(userId)
        var rank = rankService.selectByUserIdAndWeek(userId, timeHelper.getNowWeek())

        // Defender start
        defenderMap[userId]?.let {
            if (now - it <= 1000 * 15) {
                throw ServiceException(CExceptionEnum.FREQUENT_OPERATION, userId)
            }
        }
        defenderMap[userId] = now
        // Defender end

        // If first sign in
        if (rank == null) {
            rank = AttendanceRank(
                null,
                userId,
                timeHelper.getNowWeek(),
                0L,
                systemInfo.term
            )
            rankService.insert(rank)
        }

        // Judging if online
        val rankDTO = RankDTO()
        if (onlineRecord == null) {
            val newRecord = AttendanceRecord(
                "${System.currentTimeMillis()}$userId",
                userId,
                System.currentTimeMillis(),
                null,
                1,
                userId,
                systemInfo.term,
                null
            )
            recordService.insert(newRecord)
        } else {
            throw ServiceException(CExceptionEnum.USER_ONLINE, userId)
        }
        BeanUtils.copyProperties(rank, rankDTO)
        rankDTO.userName = user.name
        return rankDTO
    }

    @Transactional
    open fun signOut(userId: Long): RankDTO {
        val user = getUserByUserId(userId)
            ?: throw ServiceException(CExceptionEnum.USER_ID_NO_EXIST, userId)
        val onlineRecord = recordService.getOnlineRecordByUserId(userId)
            ?: throw ServiceException(CExceptionEnum.USER_OFFLINE, userId)
        val rank = rankService.selectByUserIdAndWeek(userId, timeHelper.getNowWeek())

        onlineRecord.status = 0
        onlineRecord.end = System.currentTimeMillis()
        recordService.updateById(onlineRecord)
        rank.totalTime += onlineRecord.end!! - onlineRecord.start
        rankService.updateById(rank)

        val rankDTO = RankDTO()
        rankDTO.accumulatedTime = onlineRecord.end!! - onlineRecord.start
        BeanUtils.copyProperties(rank, rankDTO)
        rankDTO.userName = user.name
        return rankDTO
    }

    fun complaint(targetUserId: Long, operatorUserId: Long?): Any? {
        val user = getUserByUserId(targetUserId)
            ?: throw ServiceException(CExceptionEnum.USER_ID_NO_EXIST, targetUserId)
        operatorUserId ?: throw ServiceException(CExceptionEnum.USER_ID_NO_EXIST, operatorUserId)
        val onlineRecord = recordService.getOnlineRecordByUserId(targetUserId)
            ?: throw ServiceException(CExceptionEnum.USER_C_OFFLINE, targetUserId)

        onlineRecord.status = -1
        onlineRecord.end = System.currentTimeMillis()
        onlineRecord.operatorId = operatorUserId
        recordService.updateById(onlineRecord)
        threadPoolTaskExecutor.execute(EmailThread(mailService, targetUserId, "complaint.html", "[科协签到]: 举报下线通知"))
        return null
    }

    fun helpSignOut(userId: Long) {
        val onlineRecord = recordService.getOnlineRecordByUserId(userId)
        onlineRecord?.let {
            it.status = -1
            it.end = System.currentTimeMillis()
            it.operatorId = 5201314L
            recordService.updateById(it)
        }
    }

    fun getUserByUserId(userId: Long): User? {
        return userMapper.selectByUserId(userId)
    }

    @Transactional
    open fun modifyTime(operation: String, userId: Long, time: String, token: String, week: Int?): RankDTO {
        val currentWeek = week ?: timeHelper.getNowWeek()
        if (token != systemInfo.password) {
            throw ServiceException(CExceptionEnum.PASSWORD_INCORRECT)
        }

        val res = (time.toDouble() * 60 * 60 * 1000).toLong()
        val status: Int = when (operation) {
            "add" -> {
                rankMapper.add(userId, currentWeek, res)
                UserStatusEnum.SYSTEM_GIVEN.status
            }
            "sub" -> {
                rankMapper.sub(userId, currentWeek, res)
                UserStatusEnum.SYSTEM_TAKEN.status
            }
            "set" -> {
                rankMapper.set(userId, currentWeek, res)
                UserStatusEnum.SYSTEM_GIVEN.status
            }
            else -> throw ServiceException(CExceptionEnum.UNKNOWN, userId)
        }

        val rankDTO = RankDTO()
        val attendanceRank = rankService.selectByUserIdAndWeek(userId, currentWeek)
        if (attendanceRank != null) {
            BeanUtils.copyProperties(attendanceRank, rankDTO)
        } else {
            val newRank = AttendanceRank(
                null,
                userId,
                timeHelper.getNowWeek(),
                res,
                systemInfo.term
            )
            rankService.insert(newRank)
        }

        val record = AttendanceRecord(
            "${System.currentTimeMillis()}$userId",
            userId,
            System.currentTimeMillis(),
            null,
            status,
            5201314L,
            systemInfo.term,
            res
        )
        recordMapper.insert(record)
        return rankDTO
    }

    fun importUser(file: MultipartFile, password: String): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        if (systemInfo.password != password) {
            map["result"] = "密码不正确"
            return map
        }
        dataDao(file, map)
        return map
    }

    fun deleteUser(ids: String, password: String): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        if (systemInfo.password != password) {
            map["result"] = "密码不正确"
            return map
        }

        val idList = ids.split(",").mapNotNull { it.trim().toLongOrNull() }

        if (idList.isEmpty()) {
            map["result"] = "无效的用户ID"
            return map
        }

        try {
            val deleteCount = userMapper.delete(idList)
            map["result"] = "成功删除 $deleteCount 名用户"
        } catch (e: Exception) {
            e.printStackTrace()
            map["result"] = "删除过程中出现错误: ${e.message}"
        }

        return map
    }



    @Throws(IOException::class)
    fun exportUsersToCsv(response: HttpServletResponse, password: String, grade: String) {
        if (systemInfo.password != password) {
            response.setHeader("Content-Type", "application/json")
            val map = mutableMapOf<String, Any>()
            map["code"] = CExceptionEnum.PASSWORD_INCORRECT.code
            map["msg"] = CExceptionEnum.PASSWORD_INCORRECT.msg
            objectMapper.writeValue(response.outputStream, map)
            return
        }

        val filename = "users.csv"
        response.contentType = "text/csv"
        response.characterEncoding = "UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        response.setHeader("Content-Type", "text/csv; charset=UTF-8")
        response.setHeader("Content-Disposition", "attachment; filename=\"$filename\"")
        response.setHeader("Content-Type", "application/json; charset=UTF-8")

        try {
            PrintWriter(response.writer).use { writer ->
                CSVWriter(writer).use { csvWriter ->
                    val header = arrayOf("ID", "Name", "Dept", "Location", "Email", "Github ID", "Grade")
                    csvWriter.writeNext(header)

                    val users = userMapper.selectList(grade)
                    for (user in users) {
                        val data = arrayOf(
                            user.id.toString(),
                            user.name,
                            user.dept,
                            user.location,
                            user.email,
                            user.githubId,
                            user.grade
                        )
                        csvWriter.writeNext(data)
                    }
                }
            }
        } catch (e: IOException) {
            response.setHeader("Content-Type", "application/json")
            val map = mutableMapOf<String, Any>()
            map["code"] = CExceptionEnum.SERVER_INTERNAL_ERROR.code
            map["msg"] = CExceptionEnum.SERVER_INTERNAL_ERROR.msg
            objectMapper.writeValue(response.outputStream, map)
            e.printStackTrace()
        }
    }

    fun dataDao(file: MultipartFile, map: MutableMap<String, Any>) {
        val sum = arrayOf(0)
        val inCount = arrayOf(0)
        val up = arrayOf(0)
        val userInfo = mutableListOf<String>()
        try {
            EasyExcel.read(file.inputStream, User::class.java, PageReadListener<User> { dataList ->
                for (demoData in dataList) {
                    sum[0]++

                    if (isNull(demoData)) {
                        info(userInfo, demoData, "用户数据不全(除github)")
                        continue
                    }

                    if (!checkMail(demoData.email)) {
                        info(userInfo, demoData, "邮箱检验不通过")
                        continue
                    }

                    if (demoData.id.toString().length != "2000300223".length) {
                        info(userInfo, demoData, "邮箱长度不正确")
                        continue
                    }

                    val location = arrayOf("5109", "5111", "5108")
                    if (location.none { it == demoData.location }) {
                        info(userInfo, demoData, "所在位置存在问题")
                        continue
                    }

                    val dept = arrayOf("多媒体部", "软件部", "硬件部", "老人")
                    if (dept.none { it == demoData.dept }) {
                        info(userInfo, demoData, "所在部门存在问题")
                        continue
                    }

                    val userQueryWrapper = QueryWrapper<User>()
                    userQueryWrapper.eq("id", demoData.id)
                    val user = insertMapper.selectOne(userQueryWrapper)

                    if (user != null) {
                        val change = getChange(user, demoData)
                        if (change.isNotEmpty()) {
                            insertMapper.updateById(demoData)
                            info(userInfo, user, "更新了数据($change)")
                            up[0]++
                        }
                    } else {
                        insertMapper.insert(demoData)
                        inCount[0]++
                    }
                }
            }).sheet().doRead()
        } catch (e: IOException) {
            e.printStackTrace()
            map["result"] = "传入异常"
        }
        map["info"] = userInfo
        map["result"] = "总共人数:${sum[0]} 修改人数:${up[0]} 新增人数:${inCount[0]}"
    }

    fun info(list: MutableList<String>, user: User, msg: String) {
        list.add("${user.id}(${user.name}): $msg")
    }

    fun isNull(user: User): Boolean {
        return user.id == null || user.dept == null || user.location == null || user.name == null || user.email == null
    }

    fun checkMail(email: String): Boolean {
        val regEx = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$"
        return Pattern.matches(regEx, email)
    }

    fun getChange(oU: User, nU: User): List<String> {
        val info = mutableListOf<String>()
        val declaredFields = User::class.java.declaredFields
        for (f in declaredFields) {
            f.isAccessible = true
            try {
                if (f.get(oU) != f.get(nU)) {
                    if (f.name == "githubId" && f.get(nU) == null) {
                        continue
                    }
                    info.add("${f.get(oU)}->${f.get(nU)}")
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return info
    }
}
