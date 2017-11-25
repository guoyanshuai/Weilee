package com.guide.xiaoguo.weilee.mode;

/**
 * Created by Admin on 2017/11/10.
 */

public class GPS_data_mode {
    private String SN;//设备SN
    private String longitude; //经度
    private String latitude;//纬度

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
