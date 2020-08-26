package com.kakao.guide;

public class GPSHelperClass {
    String code, latitude, longitude;

    public GPSHelperClass() {
    }

    public GPSHelperClass(String code, String latitude, String longitude) {
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
