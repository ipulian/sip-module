package com.ipusoft.sip.constant;

/**
 * author : GWFan
 * time   : 4/23/21 10:08 AM
 * desc   :
 */

public class LoginCode {
    /**
     * 签入失败，重新签入
     */
    public static final int CODE_0 = 0;
    /**
     * 已签入
     */
    public static final int CODE_1 = 1;
    /**
     * 是签入请求的第一个状态码，勿在此使用login方法，否则死循环签入
     */
    public static final int CODE_M999 = -999;
    /**
     * 其他错误，不需要再尝试登陆
     */
    public static final int CODE_M1 = -1;
    /**
     * Json解析错误
     */
    public static final int CODE_M99 = -99;
    /**
     * 初始化 completed
     */
    public static final int CODE_M100 = -100;
    /**
     * 注销 completed
     */
    public static final int CODE_M200 = -200;
}
