package com.kakao.guide;

/*
*   일정스케쥴 루트를 불러와서 해당 데이터형식을 지정하는 클래스입니다.
*
* */

public class ScheduleListVO {

    private String key;     // 좌표
    private String index;    // 순서

    public ScheduleListVO(){}

    public ScheduleListVO(String key, String index) {
        this.key = key;
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}