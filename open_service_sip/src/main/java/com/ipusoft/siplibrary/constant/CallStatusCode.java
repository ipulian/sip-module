package com.ipusoft.siplibrary.constant;

/**
 * author : GWFan
 * time   : 4/23/21 10:15 AM
 * desc   :
 */

public class CallStatusCode {
    /**
     * 终端异常，需重新初始化，再呼叫
     */
    public static final int CODE_M66 = -66;
    /**
     * 分机状态错误，需尝试登陆(login即可)，再呼叫
     */
    public static final int CODE_M88 = -88;
    /**
     * Json解析错误（系统内部错误）
     */
    public static final int CODE_M99 = -99;
    /**
     * 其他错误，请联系管理员排查再使用
     */
    public static final int CODE_M1 = -1;
    /**
     * 发送按键成功
     */
    public static final int CODE_7 = 7;
    /**
     * 发送按键失败
     */
    public static final int CODE_8 = 8;
    /**
     * 无通话
     */
    public static final int CODE_0 = 0;
    /**
     * 正在外呼
     */
    public static final int CODE_1 = 1;
    /**
     * 收到来电
     */
    public static final int CODE_2 = 2;
    /**
     * 已振铃
     */
    public static final int CODE_3 = 3;
    /**
     * 响应
     */
    public static final int CODE_4 = 4;
    /**
     * 接通
     */
    public static final int CODE_5 = 5;
    /**
     * 挂断
     */
    public static final int CODE_6 = 6;
}
