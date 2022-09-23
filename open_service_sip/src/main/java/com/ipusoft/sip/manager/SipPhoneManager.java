package com.ipusoft.sip.manager;

import com.ipusoft.context.bean.SeatInfo;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.HttpStatus;
import com.ipusoft.logger.XLogger;
import com.ipusoft.mmkv.datastore.CommonDataRepo;
import com.ipusoft.sip.SipCacheApp;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.sip.constant.SipPhoneType;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.StringUtils;

/**
 * author : GWFan
 * time   : 6/1/21 2:39 PM
 * desc   :
 */

public class SipPhoneManager {
    private static final String TAG = "SipPhoneManager";
    private static boolean flag = false;

    //TODO
    private static String extend = "";

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
    public static void callPhoneBySipForLianLian(String cPhone) {
        flag = true;
        extend = "";
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            String status = seatInfo.getHttpStatus();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                XLogger.d("callPhoneBySip：" + cPhone);
                if (StringUtils.isNotEmpty(cPhone)) {
                    String str = SipManager.getInstance().makeCall(cPhone);
                    setCallId(str);
                }
            } else {
                XLogger.d(seatInfo.getMsg());
                ToastUtils.showMessage(seatInfo.getMsg());
            }
        } else {
            XLogger.d("查询坐席信息失败1");
            ToastUtils.showMessage("查询坐席信息失败");
        }
    }

    public static void callPhoneBySip(String cPhone) {
        SipCallOutInfoBean sipCallOutInfoBean = new SipCallOutInfoBean();
        sipCallOutInfoBean.setPhone(cPhone);
        sipCallOutInfoBean.setSipPhoneType(SipPhoneType.CALL_OUT);
        SipCacheApp.setSIPCallOutBean(sipCallOutInfoBean);

        extend = "";

        flag = true;
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            String status = seatInfo.getHttpStatus();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                XLogger.d("callPhoneBySip：" + cPhone);
                if (StringUtils.isNotEmpty(cPhone)) {
                    String str = SipManager.getInstance().makeCall(cPhone);
                    setCallId(str);
                }
            } else {
                XLogger.d(seatInfo.getMsg());
                ToastUtils.showMessage(seatInfo.getMsg());
            }
        } else {
            XLogger.d("查询坐席信息失败1");
            ToastUtils.showMessage("查询坐席信息失败");
        }
    }


    public static void callPhoneBySip(String cPhone, String ex) {
        SipCallOutInfoBean sipCallOutInfoBean = new SipCallOutInfoBean();
        sipCallOutInfoBean.setPhone(cPhone);
        sipCallOutInfoBean.setSipPhoneType(SipPhoneType.CALL_OUT);
        SipCacheApp.setSIPCallOutBean(sipCallOutInfoBean);

        extend = ex;

        flag = true;
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())
                && StringUtils.isNotEmpty(seatInfo.getPassword())) {
            String status = seatInfo.getHttpStatus();
            if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                XLogger.d("callPhoneBySip：" + cPhone);
                if (StringUtils.isNotEmpty(cPhone)) {
                    String str = SipManager.getInstance().makeCall(cPhone, extend);
                    setCallId(str);
                }
            } else {
                XLogger.d(seatInfo.getMsg());
                ToastUtils.showMessage(seatInfo.getMsg());
            }
        } else {
            XLogger.d("查询坐席信息失败1");
            ToastUtils.showMessage("查询坐席信息失败");
        }
    }


    public static void setCallId(String str) {
        if (StringUtils.isNotEmpty(str)) {
            String[] s = str.split("_");
            if (s.length >= 4) {
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
                String status = seatInfo.getHttpStatus();
                if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                    String cPhone = SipCacheApp.getSIPCallOutNumber();
                    XLogger.d("reCallPhoneBySip：" + cPhone);
                    if (StringUtils.isNotEmpty(cPhone)) {
                        String str = "";
                        if (StringUtils.isNotEmpty(extend)) {
                            str = SipManager.getInstance().makeCall(cPhone, extend);
                            extend = "";
                        } else {
                            str = SipManager.getInstance().makeCall(cPhone);
                        }
                        setCallId(str);
                    }
                } else {
                    XLogger.d(seatInfo.getMsg());
                    ToastUtils.showMessage(seatInfo.getMsg());
                }
            } else {
                XLogger.d("查询坐席信息失败");
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
     * @param str 按键key
     */
    public static void dialDTMF(String str) {
        SipManager.getInstance().dialDTMF(str);
    }

    /**
     * 接听
     */
    public static void answerCall() {
        SipManager.getInstance().answerCall();
    }

    /**
     * 注册SIP
     */
    public static void registerSip() {
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                && StringUtils.isNotEmpty(seatInfo.getApiKey())) {
            SipManager.getInstance().registerSip(seatInfo);
        } else {
            XLogger.d("注册线路失败,无坐席信息,seatInfo：" + GsonUtils.toJson(seatInfo));
            ToastUtils.showMessage("注册线路失败,无坐席信息");
        }
    }
}
