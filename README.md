# 科协签到系统

这是一个面向桂林电子科技大学三院科协20级的签到系统（后端部分）

## API

所有的接口返回都长下面这个样子，其中`code`储存的是响应代码，`msg`储存的是响应消息

```json
{
    "data": xxx,
    "code": xxx,
    "msg": "xxx"
}
```

所有成功的返回值都是0，所有异常的返回值都是负数

注意，下面的所有学号姓名稍微经过了一定处理，如果你是大一的同学想借以该后端开发，我们非常欢迎哈哈哈，但是请选择正确的学号进行测试

----
### 签到
POST：`/api/user/signIn`
```json
{
    "userId":"190031022"
}
```
Response
```json
{
    "data": {
        "userId": 190031022,
        "userName": "唤雨考拉",
        "totalTime": "44.99",
        "week": 1
    },
    "code": 0,
    "msg": "签到成功"
}
```
如果重复签到
```json
{
    "code": -201,
    "msg": "不许重复签到"
}
```
---
### 签退
POST: `/api/user/signOut`
```json
{
    "userId":"190031022"
}
```
Response
```json
{
    "data": {
        "userId": 190031022,
        "userName": "唤雨考拉",
        "totalTime": "45.02",
        "accumulatedTime": "0.03",
        "week": 1
    },
    "code": 0,
    "msg": "签退成功"
}
```
如果没有签到
```json
{
    "code": -202,
    "msg": "宁没有签到噢"
}
```
---
### 获取有效排行榜前五
指如果前五有不是20级的成员，在多展示一位（最多展示10个人）

GET: `/api/record/topFive`

```json
{
    "data": [
        {
            "userId": 190030102,
            "userName": "路陈",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "totalTime": "276.00",
            "week": 1
        },
        {
            "userId": 140040404,
            "userName": "Mensu",
            "userDept": "软件部",
            "userLocation": "5108",
            "totalTime": "100.38",
            "week": 1
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "totalTime": "45.02",
            "week": 1
        },
        {
            "userId": 190012536,
            "userName": "FengChe",
            "userDept": "硬件部",
            "userLocation": "5109",
            "totalTime": "1.00",
            "week": 1
        },
        {
            "userId": 190030907,
            "userName": "Legal",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "totalTime": "0.06",
            "week": 1
        },
        {
            "userId": 1900SB106,
            "userName": "Yuxi",
            "userDept": "多媒体部",
            "userLocation": "5102",
            "totalTime": "0.00",
            "week": 1
        }
    ],
    "code": 0,
    "msg": "成功获取有效排行榜"
}
```

---

### 获取当前在线用户

GET: `/api/record/online`

```json
{
    "data": [
        {
            "userId": 190891226,
            "userName": "Xinjiang",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "status": 0
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "status": 0
        }
    ],
    "code": 0,
    "msg": "成功获取在线用户列表"
}
```

---

### 获取某个用户是否在线

GET: `/api/record/online/{userId}`

例如：`/api/record/online/190031022`

```json
{
    "data": {
        "userId": 190031022,
        "userName": "唤雨考拉",
        "status": 0 //不在线
    },
    "code": 0,
    "msg": "成功获取用户在线状态"
}
```

```json
{
    "data": {
        "userId": 190012536,
        "userName": "FengChe",
        "start": "2020-10-30 11:27:01",
        "status": 1 // 在线
    },
    "code": 0,
    "msg": "成功获取用户在线状态"
}
```

---

### 获取某个人所有的签到记录（佛系接口，因为没有做分页，后期可能数据量巨大）==可以先不用理他==

GET: `/api/record/{userId}`

例如：`/api/record/190031022`

```json
{
    "data": [
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-27 14:06:43",
            "status": "在线"
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-27 14:03:22",
            "end": "2020-10-27 14:05:09",
            "status": "已签退"
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-25 18:12:06",
            "end": "2020-10-26 15:29:37",
            "status": "已签退"
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-25 18:11:28",
            "end": "2020-10-27 14:03:13",
            "status": "已签退"
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-25 18:02:31",
            "end": "2020-10-25 18:11:27",
            "status": "被迫下线"
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-25 15:40:43",
            "end": "2020-10-25 18:02:29",
            "status": "已签退"
        },
        {
            "userId": 190031022,
            "userName": "唤雨考拉",
            "userDept": "多媒体部",
            "userLocation": "5108",
            "start": "2020-10-25 15:40:18",
            "end": "2027-02-27 06:40:18",
            "status": "已签退"
        }
    ],
    "code": 0,
    "msg": "成功获取个人数据"
}
```

---
### 其他
如果学号不正确
```json
{
    "code": -203,
    "msg": "学号不存在"
}
```
如果非常不幸，遇见了我暂时想不到的错误
```json
{
    "code": -1,
    "msg": "Unknown exception"
}
```

## 数据库



所有数据库。邮箱等连接密码，没有提交奥