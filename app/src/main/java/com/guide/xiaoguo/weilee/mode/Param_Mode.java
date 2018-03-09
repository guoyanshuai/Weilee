package com.guide.xiaoguo.weilee.mode;

import com.guide.xiaoguo.weilee.utils.Tools;

/**
 * Created by Admin on 2018/2/28.
 */

public class Param_Mode {

    private static String Package_body;

    public static String getDelayInterval(String Sn, String UpInterval) {

        Package_body = "68" + Sn + "680405600000" + UpInterval + "0000";

        String UpIntervalTime = Package_body + Tools.makeChecksum(Package_body) + "16";
        return UpIntervalTime;
    }

    public static String getCaptureTime(String Sn, String CaptureTime) {

        Package_body = "68" + Sn + "680405600100" + CaptureTime + "0000";

        String CaptureDataTime = Package_body + Tools.makeChecksum(Package_body) + "16";
        return CaptureDataTime;
    }

    public static String getExcCaptureTime(String Sn, String ExcCaptureTime) {

        Package_body = "68" + Sn + "680405601600" + ExcCaptureTime + "0000";

        String ExcUpIntervalTime = Package_body + Tools.makeChecksum(Package_body) + "16";
        return ExcUpIntervalTime;
    }

    public static String getMessageInterval(String Sn, String MessageInterval) {

        Package_body = "68" + Sn + "680405600400" + MessageInterval + "0000";

        String ExcMessageInterval = Package_body + Tools.makeChecksum(Package_body) + "16";
        return ExcMessageInterval;
    }

    public static String getMessageSwitch(String Sn, String MessageSwitch) {

        Package_body = "68" + Sn + "680404600600" + MessageSwitch + "0000";

        String ExcMessageSwitch = Package_body + Tools.makeChecksum(Package_body) + "16";
        return ExcMessageSwitch;
    }

    public static String getSoundSwitch(String Sn, String SoundSwitch) {

        Package_body = "68" + Sn + "680404600500" + SoundSwitch + "0000";

        String ExcSoundSwitch = Package_body + Tools.makeChecksum(Package_body) + "16";
        return ExcSoundSwitch;
    }

    public static String getDeviceTime(String Sn, String DeviceTime) {

        Package_body = "68" + Sn + "680409601500" + DeviceTime + "0000";

        String RTDeviceTime = Package_body + Tools.makeChecksum(Package_body) + "16";
        return RTDeviceTime;
    }

    public static String getTel1(String Sn, String Tel1) {

        Package_body = "68" + Sn + "680409600801" + Tel1 + "0000";

        String AlarmTel1 = Package_body + Tools.makeChecksum(Package_body) + "16";
        return AlarmTel1;
    }

    public static String getTel2(String Sn, String Tel2) {

        Package_body = "68" + Sn + "680409600802" + Tel2 + "0000";

        String AlarmTel2 = Package_body + Tools.makeChecksum(Package_body) + "16";
        return AlarmTel2;
    }

    public static String getTel3(String Sn, String Tel3) {

        Package_body = "68" + Sn + "680409600803" + Tel3 + "0000";

        String AlarmTel3 = Package_body + Tools.makeChecksum(Package_body) + "16";
        return AlarmTel3;
    }

    public static String getTel4(String Sn, String Tel4) {

        Package_body = "68" + Sn + "680409600804" + Tel4 + "0000";

        String AlarmTel4 = Package_body + Tools.makeChecksum(Package_body) + "16";
        return AlarmTel4;
    }

    public static String getTel5(String Sn, String Tel5) {

        Package_body = "68" + Sn + "680409600805" + Tel5 + "0000";

        String AlarmTel5 = Package_body + Tools.makeChecksum(Package_body) + "16";
        return AlarmTel5;
    }

    public static String getTempCalibration(String Sn, String Calibration) {

        Package_body = "68" + Sn + "680405600900" + Calibration + "0000";

        String TempCalibration = Package_body + Tools.makeChecksum(Package_body) + "16";
        return TempCalibration;
    }

    public static String getHumCalibration(String Sn, String Calibration) {

        Package_body = "68" + Sn + "680405600A00" + Calibration + "0000";

        String HumCalibration = Package_body + Tools.makeChecksum(Package_body) + "16";
        return HumCalibration;
    }

    public static String getTempUpLimit(String Sn, String UpLimit) {

        Package_body = "68" + Sn + "680405600B00" + UpLimit + "0000";

        String TempUpLimit = Package_body + Tools.makeChecksum(Package_body) + "16";
        return TempUpLimit;
    }

    public static String getTempDownLimit(String Sn, String DownLimit) {

        Package_body = "68" + Sn + "680405600F00" + DownLimit + "0000";

        String TempDownLimit = Package_body + Tools.makeChecksum(Package_body) + "16";
        return TempDownLimit;
    }

    public static String getHumUpLimit(String Sn, String UpLimit) {

        Package_body = "68" + Sn + "680405600C00" + UpLimit + "0000";

        String HumUpLimit = Package_body + Tools.makeChecksum(Package_body) + "16";
        return HumUpLimit;
    }

    public static String getHumDownLimit(String Sn, String DownLimit) {

        Package_body = "68" + Sn + "680405601000" + DownLimit + "0000";

        String HumDownLimit = Package_body + Tools.makeChecksum(Package_body) + "16";
        return HumDownLimit;
    }

    public static String getIpAndPort(String Sn, String IpAndPort) {

        Package_body = "68" + Sn + "680409600E00" + IpAndPort + "0000";

        String IpAndPorts = Package_body + Tools.makeChecksum(Package_body) + "16";
        return IpAndPorts;
    }

    public static String getDeviceName(String Sn, String Name) {
        int number = 4 + Name.length() / 2;
        String num = Integer.toHexString(number);
        if (num.length() < 2) {
            num = "0" + num;
        }
        int leng = Name.length() / 2;
        String len = Integer.toHexString(leng);
        if (len.length() < 2) {
            len = "0" + len;
        }
        Package_body = "68" + Sn + "6804" + num + "601300" + len + Name + "0000";

        String DeviceName = Package_body + Tools.makeChecksum(Package_body) + "16";
        return DeviceName;
    }

    public static String getCompanyName(String Sn, String Name) {
        int number = 4 + Name.length() / 2;
        String num = Integer.toHexString(number);
        if (num.length() < 2) {
            num = "0" + num;
        }
        int leng = Name.length() / 2;
        String len = Integer.toHexString(leng);
        if (len.length() < 2) {
            len = "0" + len;
        }
        Package_body = "68" + Sn + "6804" + num + "601200" + len + Name + "0000";

        String CompanyName = Package_body + Tools.makeChecksum(Package_body) + "16";
        return CompanyName;
    }

    public static String getBLEName(String Sn, String Name) {
        int number = 4 + Name.length() / 2;
        String num = Integer.toHexString(number);
        if (num.length() < 2) {
            num = "0" + num;
        }
        int leng = Name.length() / 2;
        String len = Integer.toHexString(leng);
        if (len.length() < 2) {
            len = "0" + len;
        }
        Package_body = "68" + Sn + "6804" + num + "601700" + len + Name + "0000";

        String CompanyName = Package_body + Tools.makeChecksum(Package_body) + "16";
        return CompanyName;
    }

    public static String getDeviceMode(String Sn, String mode) {
        Package_body = "68" + Sn + "680405601800" + mode + "0000";

        String Device_mode = Package_body + Tools.makeChecksum(Package_body) + "16";
        return Device_mode;
    }
    public static String getBattery(String Sn, String battery) {
        Package_body = "68" + Sn + "680405600D00" + battery + "0000";

        String Device_mode = Package_body + Tools.makeChecksum(Package_body) + "16";
        return Device_mode;
    }

    public static String QureyCompany = "68010316080001680A0360120000007216";
    public static String QureyParam1 = "68010316080001680A0360130100007416";
    public static String QureyParam2 = "68010316080001680A0360130200007516";
    public static String QureyParam3 = "68010316080001680A0360130300007616";
    public static String ReadSN = "ReadSN";
    public static String ReadVersion = "ReadVersion";
    public static String EraseHistory = "EraseHistory";
    public static String ErasePrinter = "ErasePrinter";
    public static String ReadPrinter = "ReadPrinter";
    public static String Read = "read";

    private static String getSN(String Sn) {
        String SN = "23" + Sn + "2A";
        return SN;
    }
}
