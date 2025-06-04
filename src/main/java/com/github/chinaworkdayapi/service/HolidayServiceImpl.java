package com.github.chinaworkdayapi.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.chinaworkdayapi.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class HolidayServiceImpl implements HolidayService{
    private String holidayList;
    private List<String> holidayStringList = new ArrayList<>();
    private List<String> compDayStringList = new ArrayList<>();
    
    @Resource
    private RedisUtils redisUtils;
    
    private static final String REDIS_KEY_BIG_SMALL_COUNT = "bigSmallCount";
    private static final String REDIS_KEY_BIG_SMALL_SIZE = "bigSmallSize";

    @PostConstruct
    private void init(){
        // 初始化 Redis 中的值
        if (!redisUtils.hasKey(REDIS_KEY_BIG_SMALL_COUNT)) {
            redisUtils.set(REDIS_KEY_BIG_SMALL_COUNT, "-1");
        }
        if (!redisUtils.hasKey(REDIS_KEY_BIG_SMALL_SIZE)) {
            redisUtils.set(REDIS_KEY_BIG_SMALL_SIZE, "1");
        }
        
        if (!StringUtils.hasLength(holidayList)){
            holidayList = HttpUtil.get(
                "https://www.shuyz.com/githubfiles/china-holiday-calender/master/holidayAPI.json");
        }
        JSONObject jsonObject = JSONUtil.parseObj(holidayList);
        updateStringList(jsonObject);
    }

    @Scheduled(cron = "0 0 10 * * MON")
    public void executeTask() {
        int bigSmallCount = getBigSmallCount();
        if (bigSmallCount == -1){
            return;
        }
        if (bigSmallCount > 0){
            setBigSmallCount(bigSmallCount - 1);
        }else {
            setBigSmallCount(getBigSmallSize());
        }
    }

    private void refreshHoliday(String yyyy) {
        JSONObject jsonObject = JSONUtil.parseObj(holidayList);
        String year = DateUtil.year(new Date()) + "";
        if (!year.equals(yyyy)) {
            holidayList = HttpUtil.get(
                "https://www.shuyz.com/githubfiles/china-holiday-calender/master/holidayAPI.json");
            updateStringList(jsonObject);
        }
    }

    private void updateStringList(JSONObject jsonObject) {
        holidayStringList = new ArrayList<>();
        compDayStringList = new ArrayList<>();
        JSONArray arrays = jsonObject.getJSONObject("Years")
            .getJSONArray(DateUtil.year(new Date()) + "");
        for (Object array : arrays) {
            JSONObject jo = (JSONObject)array;
            // 将字符串转换为 DateTime 对象
            DateTime startDate = DateUtil.parseDate(jo.getStr("StartDate"));
            DateTime endDate = DateUtil.parseDate(jo.getStr("EndDate"));

            // 存储日期字符串的集合
            List<String> dateList = new ArrayList<>();

            // 从开始日期到结束日期遍历
            DateTime currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                // 将当前日期格式化为 yyyy-MM-dd 并加入集合
                dateList.add(DateUtil.formatDate(currentDate));
                // 日期加一天
                currentDate = DateUtil.offsetDay(currentDate, 1);
            }
            holidayStringList.addAll(dateList);
            compDayStringList.addAll(jo.getBeanList("CompDays",String.class));
        }
    }

    @Override
    public boolean isWorkday() {
        refreshHoliday(DateUtil.year(DateUtil.date()) + "");
        // 如果明天是节日那么返回false
        String tomorrowStr = DateUtil.formatDate(DateUtil.tomorrow());
        if (holidayStringList.contains(tomorrowStr)){
            return false;
        }
        if (compDayStringList.contains(tomorrowStr)){
            return true;
        }
        // 获取明天是星期几
        LocalDate tomorrow = LocalDate.parse(tomorrowStr);
        DayOfWeek dayOfWeek = tomorrow.getDayOfWeek();

        // 判断大小周
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            if (getBigSmallCount() == 0){
                return true;
            }else {
                return false;
            }
        }
        // 判断是否是周日
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isWorkday(String workDay) {
        refreshHoliday(workDay.substring(0,4));
        // 如果今天是节日那么返回false
        String todayStr = workDay;
        if (holidayStringList.contains(todayStr)){
            return false;
        }
        if (compDayStringList.contains(todayStr)){
            return true;
        }
        // 获取今天是星期几
        LocalDate today = LocalDate.parse(todayStr);
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // 判断大小周
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            long l = DateUtil.betweenWeek(new Date(), DateUtil.parseDate(todayStr), true);
            int bigCount = getBigSmallCount();
            for (long i = 0; i < l; i++) {
                if (bigCount > 0){
                    bigCount--;
                }else {
                    bigCount = getBigSmallSize();
                }
            }
            if (bigCount == 0){
                return true;
            }else {
                return false;
            }
        }
        // 判断是否是周日
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        return true;
    }

    @Override
    public void setBigSmallCount(int bigSmallCount) {
        redisUtils.set(REDIS_KEY_BIG_SMALL_COUNT, String.valueOf(bigSmallCount));
    }

    @Override
    public void setBigSmallSize(int bigSmallSize) {
        redisUtils.set(REDIS_KEY_BIG_SMALL_SIZE, String.valueOf(bigSmallSize));
    }

    @Override
    public int getBigSmallCount() {
        Object value = redisUtils.get(REDIS_KEY_BIG_SMALL_COUNT);
        return value == null ? -1 : Integer.parseInt(value.toString());
    }

    @Override
    public int getBigSmallSize() {
        Object value = redisUtils.get(REDIS_KEY_BIG_SMALL_SIZE);
        return value == null ? 1 : Integer.parseInt(value.toString());
    }
}
