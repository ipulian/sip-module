package com.ipusoft.siplibrary.manager;


import android.util.Log;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.config.Env;
import com.ipusoft.context.http.HttpConstant;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.siplibrary.MyLogWriter;
import com.ipusoft.siplibrary.MySipPhoneEvent;
import com.ipusoft.siplibrary.bean.SeatInfo;

import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.Phone;
import org.pjsip.pjsua2.PhoneConfig;


/**
 * author : GWFan
 * time   : 1/12/21 8:22 PM
 * desc   : SIP管理类
 */

public class SipManager {
    private static final String TAG = "SipManager";
    protected Phone phone;
    protected PhoneConfig phoneConfig;
    protected MyLogWriter logWriter;
    protected MySipPhoneEvent mySipPhoneEvent;
    protected LogConfig logConfig;

    private SipManager() {
    }

    private static class SipManagerHolder {
        private static final SipManager INSTANCE = new SipManager();
    }

    public static SipManager getInstance() {
        return SipManagerHolder.INSTANCE;
    }

    /**
     * 注册sip
     */
    public void registerSip(SeatInfo seatInfo) {
        if (seatInfo == null || StringUtils.isEmpty(seatInfo.getSeatNo())
                || StringUtils.isEmpty(seatInfo.getSdkSecret()) ||
                StringUtils.isEmpty(seatInfo.getApiKey())
                || StringUtils.isEmpty(seatInfo.getPassword())) {
            Log.d(TAG, "registerSip: 参数错误,坐席信息不能为空");
            return;
        }
//        if (StringUtils.isEmpty(sipUrl)) {
//            Log.d(TAG, "registerSip: 参数错误,SIP线路不能为空");
//            return;
//        }
        if (logWriter == null)
            logWriter = new MyLogWriter();

        if (mySipPhoneEvent == null) {
            mySipPhoneEvent = new MySipPhoneEvent();
        }
        if (logConfig == null) {
            logConfig = new LogConfig();
            int LOG_LEVEL = 4;
            logConfig.setLevel(LOG_LEVEL);
            logConfig.setConsoleLevel(LOG_LEVEL);
            logConfig.setWriter(logWriter);
        }
        Log.d(TAG, "registerSip: --->" + GsonUtils.toJson(seatInfo));
        Log.d(TAG, "registerSip: --->" + (AppContext.getRuntimeEnv() == Env.DEV ?
                HttpConstant.OPEN_URL_DEV : HttpConstant.OPEN_URL_PRO));
        if (phoneConfig == null) {
            phoneConfig = new PhoneConfig();
            phoneConfig.setLogConfig(logConfig);
            phoneConfig.setLl_api_server(AppContext.getRuntimeEnv() == Env.DEV ?
                    HttpConstant.OPEN_URL_DEV : HttpConstant.OPEN_URL_PRO);
            phoneConfig.setApi_key(seatInfo.getApiKey());
            phoneConfig.setSdk_secret(seatInfo.getSdkSecret());
            phoneConfig.setSeatId(seatInfo.getSeatNo());
            phoneConfig.setSeatPwd(seatInfo.getPassword());
            phoneConfig.setPe(mySipPhoneEvent);
        }
        if (phone == null) {
            phone = new Phone();
        }
        phone.init(phoneConfig);
    }

    /**
     * 外呼
     *
     * @param cPhone 外呼号码
     */
    public void makeCall(String cPhone) {
        if (phone != null) {
            phone.callout(cPhone);
        }
    }

    /**
     * 挂断电话
     */
    public void dropCall() {
        if (phone != null) {
            phone.dropCall();
        }
    }

    /**
     * 注销SIP
     */
    public void unregisterSip() {
        if (phone != null) {
            phone.unInit();
        }
    }

    public void releaseRes() {
        if (phone != null) {
            phone.delete();
        }
        phoneConfig = null;
        logConfig = null;
        logWriter = null;
        phone = null;
    }

    /**
     * 登陆分机
     */
    public void login() {
        if (phone != null) {
            phone.login();
        }
    }

    /**
     * ResetRetryCount
     */
    public void resetLoginCnt() {
        if (phone != null) {
            phone.resetLoginCnt();
        }
    }

    public void dialDTMF(String digital) {
        if (phone != null) {
            phone.dialDTMF(digital);
        }
    }
}
