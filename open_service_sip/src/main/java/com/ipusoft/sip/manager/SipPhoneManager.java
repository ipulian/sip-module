package com.ipusoft.sip.manager;

import android.util.Log;
import android.widget.Toast;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.bean.SeatInfo;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.HttpStatus;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.mmkv.datastore.CommonDataRepo;
import com.ipusoft.sip.SipCacheApp;

/**
 * author : GWFan
 * time   : 6/1/21 2:39 PM
 * desc   :
 */

public class SipPhoneManager {
    private static final String TAG = "SipPhoneManager";
    private static boolean flag = false;

    public static void setFlag(boolean f) {
        flag = f;
    }

    public static boolean getFlag() {
        return flag;
    }

    /**
     * 通过SIP外呼
     *
     * @param cPhone
     */
    public static void callPhoneBySip(String cPhone) {
        flag = true;
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            String status = seatInfo.getStatus();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                if (StringUtils.isNotEmpty(cPhone)) {
                    String str = SipManager.getInstance().makeCall(cPhone);
                    setCallId(str);
                }
            } else {
                ToastUtils.showMessage(seatInfo.getMsg());
            }
        } else {
            ToastUtils.showMessage("查询坐席信息失败");
        }
    }

    public static void setCallId(String str) {
        if (StringUtils.isNotEmpty(str)) {
            String[] s = str.split("_");
            if (s != null && s.length >= 4) {
                String callId = s[3];
                AppCacheContext.setSIPCallOutId(callId);
            }
        }
    }


    public static void reCallPhoneBySip() {
        if (flag) {
            SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
            if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                    && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                    && StringUtils.isNotEmpty(seatInfo.getApiKey())
                    && StringUtils.isNotEmpty(seatInfo.getPassword())) {
                String status = seatInfo.getStatus();
                if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                    String cPhone = SipCacheApp.getSIPCallOutNumber();
                    Log.d(TAG, "callPhoneBySip:cPhone->" + cPhone);
                    if (StringUtils.isNotEmpty(cPhone)) {
                        Log.d(TAG, "callPhoneBySip: ------" + Thread.currentThread().getName());
                        String str = SipManager.getInstance().makeCall(cPhone);
                        setCallId(str);
                    }
                } else {
                    ToastUtils.showMessage(seatInfo.getMsg());
                }
            } else {
                ToastUtils.showMessage("查询坐席信息失败");
            }
        }
    }

    /**
     * 挂断SIP电话
     */
    public static void dropPhone() {
        SipManager.getInstance().dropCall();
    }

    /**
     * 发送按键
     *
     * @param str
     */
    public static void dialDTMF(String str) {
        SipManager.getInstance().dialDTMF(str);
    }

    /**
     * 注册SIP
     */
    public static void registerSip() {
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        Log.d(TAG, "registerSip: 1--------------" + GsonUtils.toJson(seatInfo));
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            SipManager.getInstance().registerSip(seatInfo);
        } else {
            Toast.makeText(AppContext.getActivityContext(), "注册线路失败,无坐席信息", Toast.LENGTH_SHORT).show();
        }
    }
}
