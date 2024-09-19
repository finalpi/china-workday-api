package com.github.chinaworkdayapi.service;

public interface HolidayService {
    boolean isWorkday();
    boolean isWorkday(String workDay);

    void setBigSmallCount(int bigSmallCount);
    void setBigSmallSize(int bigSmallSize);

    int getBigSmallCount();
    int getBigSmallSize();

}
