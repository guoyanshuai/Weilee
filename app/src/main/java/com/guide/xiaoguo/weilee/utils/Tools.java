package com.guide.xiaoguo.weilee.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.widget.EditText;
import android.widget.TextView;

import com.hh.timeselector.timeutil.datedialog.DateListener;
import com.hh.timeselector.timeutil.datedialog.TimeConfig;
import com.hh.timeselector.timeutil.datedialog.TimeSelectorDialog;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 2017/11/9.
 */

public class Tools {
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
//       int height = bm.getHeight();宽高等比例放大
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / width;
//        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, width, matrix, true);
        //Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=GBK");
    OkHttpClient okHttpClient = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(100, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(60, TimeUnit.SECONDS);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public void GetDataTime(Context context, String time, final EditText textView) {
        TimeSelectorDialog dialog = new TimeSelectorDialog(context);
        //设置标题
        dialog.setTimeTitle("选择时间:");
        //显示类型
        dialog.setIsShowtype(TimeConfig.YEAR_MONTH_DAY_HOUR_MINUTE);
        //默认时间
        dialog.setCurrentDate(time);
        //隐藏清除按钮
        dialog.setEmptyIsShow(false);
        //设置起始时间
        dialog.setStartYear(1888);
        dialog.setDateListener(new DateListener() {
            @Override
            public void onReturnDate(String time, int year, int month, int day, int hour, int minute, int isShowType) {
                textView.setText(time);
            }

            @Override
            public void onReturnDate(String empty) {
            }
        });
        dialog.show();

    }

    public String getCurrentTime() {
        Date datetime = new Date(System.currentTimeMillis());
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sDateFormat.format(datetime);
        return time.substring(0, 16);
    }

    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        } /** * 用256求余最大是255，即16进制的FF */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length(); // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) { hex = "0" + hex; }
        return hex;
         }

    private static String hexString="0123456789ABCDEF";
    public static String encode(String str, String charset) throws UnsupportedEncodingException {
        //根据默认编码获取字节数组
        byte[] bytes=str.getBytes(charset);
        StringBuilder sb=new StringBuilder(bytes.length*2);
        //将字节数组中每个字节拆解成2位16进制整数
        for(int i=0;i<bytes.length;i++){
            sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
            sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
        }
        return sb.toString();
    }

    public static String DecTo1ByteHexString(int b) {
        String r = "";
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        r += hex.toUpperCase();
        return r;
    }
    public static String decTo2ByteHex(int dec) {
        String hex = "";
        while (dec != 0) {
            String h = Integer.toString(dec & 0xff, 16);
            if ((h.length() & 0x01) == 1)
                h = '0' + h;
            hex = hex + h;
            dec = dec >> 8;
        }
        return hex;
    }

    }
