package com.ipusoft.sip.manager;


import android.util.Log;

import com.ipusoft.context.AppRuntimeContext;
import com.ipusoft.context.bean.SeatInfo;
import com.ipusoft.logger.XLogger;
import com.ipusoft.sip.MyLogWriter;
import com.ipusoft.sip.MySipPhoneEvent;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.StringUtils;

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
                StringUtils.isEmpty(seatInfo.getApiKey())) {
            XLogger.d("registerSip: 参数错误,坐席信息不能为空,seatInfo：" + GsonUtils.toJson(seatInfo));
            return;
        }
        Log.d(TAG, "registerSip: .--------->" + GsonUtils.toJson(seatInfo));
        if (logWriter == null)
            logWriter = new MyLogWriter();

        if (mySipPhoneEvent == null) {
            mySipPhoneEvent = new MySipPhoneEvent();
        }
        if (logConfig == null) {
            logConfig = new LogConfig();
            int LOG_LEVEL = 5;
            logConfig.setLevel(LOG_LEVEL);
            logConfig.setConsoleLevel(LOG_LEVEL);
            logConfig.setWriter(logWriter);
        }
        if (phoneConfig == null) {
            phoneConfig = new PhoneConfig();
            phoneConfig.setLogConfig(logConfig);
            phoneConfig.setLl_api_server(AppRuntimeContext.OPEN_BASE_URL);

            Log.d(TAG, "registerSip: ----------->" + AppRuntimeContext.OPEN_BASE_URL);
//            phoneConfig.setLl_api_server("https://api.51lianlian.cn");
            // Log.d(TAG, "registerSip: .----------->" + GsonUtils.toJson(seatInfo));
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
    public String makeCall(String cPhone) {
        XLogger.d("makeCall：" + cPhone);
        String recordId = "";
        if (phone != null) {
            recordId = phone.callout(cPhone);
            //recordId = phone.callout(cPhone, "123455678");
        }
        XLogger.d("makeCall->recordId：" + recordId);
        return recordId;
    }

    public String makeCall(String cPhone, String extend) {
        XLogger.d("makeCall：" + cPhone);
        String recordId = "";
        if (phone != null) {
            // recordId = phone.callout(cPhone);
            recordId = phone.callout(cPhone, extend);
        }
        XLogger.d("makeCall->recordId：" + recordId);
        return recordId;
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
        mySipPhoneEvent = null;
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

    /**
     * 按键
     *
     * @param digital 按键key
     */
    public void dialDTMF(String digital) {
        if (phone != null) {
            phone.dialDTMF(digital);
        }
    }

    /**
     * 接听
     */
    public void answerCall() {
        if (phone != null) {
            phone.answerCall();
        }
    }

    public void libRegisterThread(String name) {
        if (phone != null) {
            phone.libRegisterThread(name);
        }
    }
}
