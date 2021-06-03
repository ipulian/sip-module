package com.ipusoft.siplibrary.manager;

import android.util.Log;
import android.widget.Toast;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.HttpStatus;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.siplibrary.SipCacheApp;
import com.ipusoft.siplibrary.bean.SeatInfo;
import com.ipusoft.siplibrary.datastore.SipDataStore;

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
        SeatInfo seatInfo = SipDataStore.getSeatInfo();
        if (seatInfo != null) {
            String status = seatInfo.getStatus();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                Log.d(TAG, "callPhoneBySip:cPhone->" + cPhone);
                if (StringUtils.isNotEmpty(cPhone)) {
                    ToastUtils.showLoading();
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

    public static void reCallPhoneBySip() {
        SeatInfo seatInfo = SipDataStore.getSeatInfo();
        if (seatInfo != null) {
            String status = seatInfo.getStatus();
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
        SeatInfo seatInfo = SipDataStore.getSeatInfo();
        if (seatInfo != null && StringUtils.equals(HttpStatus.SUCCESS, seatInfo.getStatus())) {
            SipManager.getInstance().registerSip(seatInfo);
        } else {
            Toast.makeText(AppContext.getActivityContext(), "注册线路失败,无坐席信息", Toast.LENGTH_SHORT).show();
        }
    }
}
