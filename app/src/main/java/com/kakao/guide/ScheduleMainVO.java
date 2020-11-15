package com.kakao.guide;

public class ScheduleMainVO {
    private String scheduleAdd;
    private String sheduleName;
    private String scheduleGPS;

    public ScheduleMainVO(String scheduleAdd, String sheduleName, String scheduleGPS) {
        this.scheduleAdd = scheduleAdd;
        this.sheduleName = sheduleName;
        this.scheduleGPS = scheduleGPS;
    }

    public String getScheduleAdd() {
        return scheduleAdd;
    }
    public void setScheduleAdd(String scheduleAdd) {
        this.scheduleAdd = scheduleAdd;
    }

    public String getSheduleName() {
        return sheduleName;
    }
    public void setSheduleName(String sheduleName) {
        this.sheduleName = sheduleName;
    }

    public String getScheduleGPS() {
        return scheduleGPS;
    }
    public void setScheduleGPS(String scheduleGPS) {
        this.scheduleGPS = scheduleGPS;
    }
}
