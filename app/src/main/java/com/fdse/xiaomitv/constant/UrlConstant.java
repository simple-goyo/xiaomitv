package com.fdse.xiaomitv.constant;


/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/08/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class UrlConstant {

    /**
     * App后端url
     */
    public static final String APP_BACK_END_IP = "10.141.221.88";//142,148110
    public static final String APP_BACK_END_PORT = "8080/sc";
//    public static final String APP_BACK_END_IP = "192.168.1.105";//142,148110
//    public static final String APP_BACK_END_PORT = "8080/";

    //用户登录1
    public static final String APP_BACK_END_TV_GET_TV_TASK = "device/getTVTask";


    public static String getAppBackEndServiceURL(String service) {
        String serviceURL = String.format("http://%s:%s/%s", APP_BACK_END_IP, APP_BACK_END_PORT, service);
        return serviceURL;
    }

}
