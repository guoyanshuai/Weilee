package com.guide.xiaoguo.weilee.mode;

/**
 * Created by Admin on 2017/11/10.
 */

public class RTorHis_data_mode {
    private String SN;      //设备的SN
    private String DeviceName;  //设备的名称
    private String status;//数据的状态
    private String param1;//温度1
    private String param2;//湿度1
    private String param3;//温度2
    private String param4;//湿度2
    private String upDataTime;//数据日期

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    public String getUpDataTime() {
        return upDataTime;
    }

    public void setUpDataTime(String upDataTime) {
        this.upDataTime = upDataTime;
    }


}
