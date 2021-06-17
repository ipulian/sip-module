package com.ipusoft.sip.manager;

import android.util.Log;
import android.widget.Toast;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.bean.SeatInfo;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.HttpStatus;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.mmkv.datastore.AppDataStore;
import com.ipusoft.sip.SipCacheApp;

/**
 * author : GWFan
 * time   : 6/1/21 2:39 PM
 * desc   :
 */

public class SipPhoneManager {
    private static final String TAG = "SipPhoneManager";

    /**
     * 通过SIP外呼
     *
     * @param cPhone
     */
    public static void callPhoneBySip(String cPhone) {
        Log.d(TAG, "callPhoneBySip: -cPhone-------》" + cPhone);
        SeatInfo seatInfo = AppDataStore.getSeatInfo();
        Log.d(TAG, "callPhoneBySip: --------123");
        Log.d(TAG, "callPhoneBySip: -----------。" + GsonUtils.toJson(seatInfo));
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            String status = seatInfo.getStatus();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                //ToastUtils.dismiss();
                Log.d(TAG, "callPhoneBySip: 3333" + cPhone);
                if (StringUtils.isNotEmpty(cPhone)) {
                    SipManager.getInstance().makeCall(cPhone);
                }
            } else {
                Log.d(TAG, "callPhoneBySip: 444");
                //  Log.d(TAG, "callPhoneBySip: ----------》");
                ToastUtils.showMessage(seatInfo.getMsg());
            }
        } else {
            ToastUtils.showMessage("查询坐席信息失败");
        }
    }

    public static void reCallPhoneBySip() {
        SeatInfo seatInfo = AppDataStore.getSeatInfo();
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            String status = seatInfo.getStatus();
            ToastUtils.dismiss();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                String cPhone = SipCacheApp.getSIPCallOutNumber();
                Log.d(TAG, "callPhoneBySip:cPhone->" + cPhone);
                if (StringUtils.isNotEmpty(cPhone)) {
                    Log.d(TAG, "callPhoneBySip: ------" + Thread.currentThread().getName());
                    SipManager.getInstance().makeCall(cPhone);
                }
            } else {
                ToastUtils.showMessage(seatInfo.getMsg());
            }
        } else {
            ToastUtils.showMessage("查询坐席信息失败");
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
        SeatInfo seatInfo = AppDataStore.getSeatInfo();
        if (seatInfo != null && StringUtils.equals(HttpStatus.SUCCESS, seatInfo.getStatus())
                && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            SipManager.getInstance().registerSip(seatInfo);
        } else {
            Toast.makeText(AppContext.getActivityContext(), "注册线路失败,无坐席信息", Toast.LENGTH_SHORT).show();
        }
    }
}
