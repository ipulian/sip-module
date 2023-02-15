package com.ipusoft.sip;

import android.util.Log;

import com.ipusoft.context.bean.SipResponse;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.component.WToast;
import com.ipusoft.context.iface.BaseSipStatusChangedListener;
import com.elvishew.xlog.XLog;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.constant.HttpCode;
import com.ipusoft.sip.constant.InitCode;
import com.ipusoft.sip.constant.LoginCode;
import com.ipusoft.sip.constant.SipPhoneType;
import com.ipusoft.sip.constant.SipType;
import com.ipusoft.sip.manager.SipManager;
import com.ipusoft.sip.manager.SipPhoneManager;
import com.ipusoft.sip.service.SipCoreService;
import com.ipusoft.utils.ArrayUtils;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.StringUtils;
import com.ipusoft.utils.ThreadUtils;

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
    }

    @Override
    public void msg(String date, String type, int code, String msg) {
        // Log.d(TAG, "msg: ------------->" + code + "---->" + msg);
        XLog.d("date：" + date + "\ntype：" + type + "\ncode：" + code + "\nmsg：" + msg + "\n");
        // if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
        XLog.d("sipStatusChangedListenerList.size：" + ArrayUtils.getListSize(sipStatusChangedListenerList));
        SipResponse sipResponse = new SipResponse();
        sipResponse.setCode(code);
        sipResponse.setDate(date);
        sipResponse.setType(type);
        sipResponse.setMsg(msg);
        preHandleStatus(type, sipResponse);
//        } else {
//            Log.d(TAG, "sipStatusChangedListenerList: -------------------");
//        }
    }

    private void preHandleStatus(String type, SipResponse sipResponse) {
        Log.d(TAG, "preHandleStatus: ------------》" + type + "---->" + GsonUtils.toJson(sipResponse));
        int code = sipResponse.getCode();
        String msg = sipResponse.getMsg();
        if (StringUtils.equals(SipType.INIT.getType(), type)) {
            if (code == InitCode.CODE_M1) {
                XLog.d("init：" + msg);
                ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
                    for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                        listener.onSipResponseError(sipResponse);
                    }
                }
            } else {
                new Thread(() -> {
                    SipManager.getInstance().libRegisterThread(Thread.currentThread().getName());
                    SipManager.getInstance().login();
                }).start();
            }
        } else if (StringUtils.equals(SipType.LOGIN.getType(), type)) {
            switch (code) {
                case LoginCode.CODE_1:
                    XLog.d("login: 已签入");
                    SipPhoneManager.reCallPhoneBySip();
                    SipCoreService.setSipStatus(1);
                    break;
                case LoginCode.CODE_0:
                    XLog.d("login: 签入失败，重新签入");
                    new Thread(() -> {
                        SipManager.getInstance().libRegisterThread(Thread.currentThread().getName());
                        SipManager.getInstance().login();
                    }).start();
                    SipCoreService.setSipStatus(0);
                    break;
                case LoginCode.CODE_M1:
                case LoginCode.CODE_M99:
                    SipCoreService.setSipStatus(0);
                    ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                    if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
                        for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                            listener.onSipResponseError(sipResponse);
                        }
                    }
                    break;
                case LoginCode.CODE_M100:
                    XLog.d("login: code=-100:uninit completed");
                    SipCoreService.setSipStatus(0);
                    SipManager.getInstance().unregisterSip();
                    break;
                case LoginCode.CODE_M200:
                    XLog.d("login: code=-200:注销 completed");
                    SipCoreService.setSipStatus(0);
                    SipManager.getInstance().releaseRes();
                    SipPhoneManager.registerSip();
                    break;
                default:
                    break;
            }
        } else if (StringUtils.equals(SipType.HTTP.getType(), type)) {
            if (code == HttpCode.CODE_M99) {
                ThreadUtils.runOnUiThread(ToastUtils::dismiss);
                XLog.d("http: code=-99:网络错误" + msg);
                if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
                    for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                        listener.onSipResponseError(sipResponse);
                    }
                }
            }
        } else if (StringUtils.equals(SipType.CALL_STATUS.getType(), type)) {
            switch (code) {
                case CallStatusCode.CODE_M66:
                    XLog.d("终端异常，需重新初始化，再呼叫");
                    break;
                case CallStatusCode.CODE_M88:

                    Log.d(TAG, "preHandleStatus: ---------");
                    XLog.d("分机状态错误，需尝试登陆(login即可)，再呼叫");
                    new Thread(() -> {
                        SipManager.getInstance().libRegisterThread(Thread.currentThread().getName());
                        SipManager.getInstance().login();
                    }).start();
                    break;
                case CallStatusCode.CODE_M99:
                    XLog.d("Json解析错误（系统内部错误）");
                    ThreadUtils.runOnUiThread(() -> ToastUtils.showMessage(msg));
                    break;
                case CallStatusCode.CODE_M1:
                    XLog.d("其他错误，请联系管理员排查再使用" + msg);
                    ThreadUtils.runOnUiThread(() -> ToastUtils.showMessage(msg));
                    break;
                case CallStatusCode.CODE_7:
                    XLog.d("发送按键成功");
                    break;
                case CallStatusCode.CODE_8:
                    XLog.d("发送按键失败");
                    ThreadUtils.runOnUiThread(() -> WToast.showMessage("发送按键失败"));
                    break;
                case CallStatusCode.CODE_0:
                case CallStatusCode.CODE_1:
                case CallStatusCode.CODE_2:
                case CallStatusCode.CODE_3:
                case CallStatusCode.CODE_4:
                case CallStatusCode.CODE_5:
                case CallStatusCode.CODE_6:

                    if (code == CallStatusCode.CODE_2) {
                        SipCallOutInfoBean sipCallOutInfoBean = new SipCallOutInfoBean();
                        if (StringUtils.isNotEmpty(msg)) {
                            String[] split = msg.split("<");
                            if (split.length != 0) {
                                String number = split[0];
                                XLog.d("msg: .-------------1>" + number);
                                if (StringUtils.isNotEmpty(number) && number.length() > 4) {
                                    String callInNumber = number.substring(1, number.length() - 2);
                                    XLog.d("msg: .------------->" + callInNumber);
                                    sipCallOutInfoBean.setPhone(callInNumber);
                                }
                            }
                        }
                        sipCallOutInfoBean.setSipPhoneType(SipPhoneType.CALL_IN);
                        SipCacheApp.setSIPCallOutBean(sipCallOutInfoBean);
                    }
                    SipPhoneManager.setFlag(false);
                    AppCacheContext.setSipState(code);
                    if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
                        for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                            listener.onSipResponseSuccess(sipResponse);
                        }
                    }
                    break;
                default:
                    SipPhoneManager.setFlag(false);
                    AppCacheContext.setSipState(code);
                    if (ArrayUtils.isNotEmpty(sipStatusChangedListenerList)) {
                        for (BaseSipStatusChangedListener listener : sipStatusChangedListenerList) {
                            listener.onSipResponseSuccess(sipResponse);
                        }
                    }
                    break;
            }
        }
    }
}
