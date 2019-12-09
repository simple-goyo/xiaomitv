package com.fdse.xiaomitv.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by lwh on 2017/6/5.
 */

public class Global {
    //保存session
    public static String sessionId = "";

    public static String getTime(Timestamp time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = format.format(time);
        return timeStr;
    }
}
