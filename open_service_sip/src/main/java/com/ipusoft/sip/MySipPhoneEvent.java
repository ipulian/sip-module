package com.ipusoft.sip;

import android.util.Log;

import com.ipusoft.context.bean.SipResponse;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.component.WToast;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.context.utils.ThreadUtils;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.constant.HttpCode;
import com.ipusoft.sip.constant.InitCode;
import com.ipusoft.sip.constant.LoginCode;
import com.ipusoft.sip.constant.SipType;
import com.ipusoft.sip.manager.SipManager;
import com.ipusoft.sip.manager.SipPhoneManager;

import org.pjsip.pjsua2.PhoneEvent;

import java.util.List;

/**
 * author : GWFan
 * time   : 1/14/21 3:23 PM
 * desc   : SIP 事件回调
 */

public class MySipPhoneEvent extends PhoneEvent {
    private static final String TAG = "MySipPhoneEvent";
    public static List<BaseSipStatusChangedListener> sipStatusChangedListenerList;

    public static void registerSipStatusChangedListener(List<BaseSipStatusChangedListener> listeners) {
        sipStatusChangedListenerList = listeners;
        Log.d(TAG, "registerSipStatusChangedListener: ------->" + sipStatusChangedListenerList.size());
    }

    @Override
    public void msg(String date, String type, int code, String msg) {
        Log.d(TAG, "msg: ---------》" + date);
        Log.d(TAG, "msg: ---------》" + type);
        Log.d(TAG, "msg: ---------》" + code);
        Log.d(TAG, "msg: ---------》" + msg);
        if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
            Log.d(TAG, "msg: ------------------sipStatusChangedListenerList.size()-》" + (sipStatusChangedListenerList.size()));
            SipResponse sipResponse = new SipResponse();
            sipResponse.setCode(code);
            sipResponse.setDate(date);
            sipResponse.setType(type);
            sipResponse.setMsg(msg);
            preHandleStatus(type, sipResponse);
        } else {
            Log.d(TAG, "msg: ------------------sipStatusChangedListenerList.size()=0");
        }
    }

    private void preHandleStatus(String type, SipResponse sipResponse) {
        int code = sipResponse.getCode();
        String msg = sipResponse.getMsg();
        Log.d(TAG, "preHandleStatus: ------->" + type + "-----》" + code);
        if (StringUtils.equals(SipType.INIT.getType(), type)) {
            if (code == InitCode.CODE_M1) {
                Log.d(TAG, "init: " + msg);
                ToastUtils.dismiss();
                for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                    listener.onSipResponseError(sipResponse);
                }
            }
        } else if (StringUtils.equals(SipType.LOGIN.getType(), type)) {
            switch (code) {
                case LoginCode.CODE_1:
                    Log.d(TAG, "login: 已签入");
                    SipPhoneManager.reCallPhoneBySip();
                    break;
                case LoginCode.CODE_0:
                    Log.d(TAG, "login: 签入失败，重新签入");
                    SipManager.getInstance().login();
                    break;
                case LoginCode.CODE_M1:
                case LoginCode.CODE_M99:
                    ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                    Log.d(TAG, "login: code=:" + code + "--->" + msg);
                    for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                        listener.onSipResponseError(sipResponse);
                    }
                    break;
                case LoginCode.CODE_M100:
                    Log.d(TAG, "login: code=-100:uninit completed");
                    SipManager.getInstance().unregisterSip();
                    break;
                case LoginCode.CODE_M200:
                    Log.d(TAG, "login: code=-200:注销 completed");
                    SipManager.getInstance().releaseRes();
                    SipPhoneManager.registerSip();
                    break;
                default:
                    break;
            }
        } else if (StringUtils.equals(SipType.HTTP.getType(), type)) {
            if (code == HttpCode.CODE_M99) {
                ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                Log.d(TAG, "http: code=-99:网络错误" + msg);
                for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                    listener.onSipResponseError(sipResponse);
                }
            }
        } else if (StringUtils.equals(SipType.CALL_STATUS.getType(), type)) {
            switch (code) {
                case CallStatusCode.CODE_M66:
                    Log.d(TAG, "onSipStatusChanged: ---->终端异常，需重新初始化，再呼叫");
                    //SipPhoneManager.registerSip();
                    break;
                case CallStatusCode.CODE_M88:
                    Log.d(TAG, "onSipStatusChanged: ---->分机状态错误，需尝试登陆(login即可)，再呼叫");
                    SipManager.getInstance().login();
                    break;
                case CallStatusCode.CODE_M99:
                    Log.d(TAG, "onSipStatusChanged: ---->Json解析错误（系统内部错误）");
                    ThreadUtils.runOnUiThread(() -> ToastUtils.showMessage(msg));
                    break;
                case CallStatusCode.CODE_M1:
                    Log.d(TAG, "onSipStatusChanged: ---->其他错误，请联系管理员排查再使用");
                    Log.d(TAG, "onSipStatusChanged: --" + msg);
                    ThreadUtils.runOnUiThread(() -> ToastUtils.showMessage(msg));
                    break;
                case CallStatusCode.CODE_7:
                    Log.d(TAG, "onSipStatusChanged: ------发送按键成功");
                    break;
                case CallStatusCode.CODE_8:
                    Log.d(TAG, "onSipStatusChanged: ------发送按键失败");
                    ThreadUtils.runOnUiThread(() -> WToast.showMessage("发送按键失败"));
                    break;
                case CallStatusCode.CODE_0:
                case CallStatusCode.CODE_1:
                case CallStatusCode.CODE_2:
                case CallStatusCode.CODE_3:
                case CallStatusCode.CODE_4:
                case CallStatusCode.CODE_5:
                case CallStatusCode.CODE_6:
                    SipPhoneManager.setFlag(false);
                    AppCacheContext.setSipState(code);
                    for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                        listener.onSipResponseSuccess(sipResponse);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
