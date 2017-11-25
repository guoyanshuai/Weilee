package com.guide.xiaoguo.weilee.mode;

/**
 * Created by Admin on 2017/11/10.
 */

public class GrouporDevice_data_mode {
    private String group_ID;
    private String SN;//设备SN
    private String deviceName;
    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }
    public String getGroup_ID() {
        return group_ID;
    }

    public void setGroup_ID(String group_ID) {
        this.group_ID = group_ID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

}
