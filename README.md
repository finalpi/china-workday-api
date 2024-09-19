# 查询是否是工作日的api

使用java实现的查询是否是工作日的api接口,支持大小周和调休,配合tasker使用可以自动设置明日的闹钟

# 接口列表
**接口名称:**

isWorkday

**接口描述:**
查询明日是否是工作日


**请求路径:**

/api/isWorkday

**请求方式:**

GET

-----------------
**接口名称:**

isWorkday

**接口描述:**
查询指定日期是否是工作日,workDay的格式为yyyy-MM-dd


**请求路径:**

/api/isWorkday/{workDay}

**请求方式:**

GET

--------

**接口名称:**

setBigSmallCount

**接口描述:**

设置大小周计数器,设定为-1则关闭大小周计数,当计数器为0代表本周是小周

**请求路径:**

/api/setBigSmallCount/{count}

**请求方式:**

POST

-------------

**接口名称:**

setBigSmallSize

**接口描述:**
设置大小周总数,为1代表一周轮换大小周,为2代表两周轮换大小周


**请求路径:**

/api/setBigSmallSize/{count}

**请求方式:**

POST

-------
**接口名称:**

getInfo

**接口描述:**
查询设置的大小周的数值


**请求路径:**

/api/getInfo

**请求方式:**

GET