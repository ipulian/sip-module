package com.ipusoft.sip.manager;

import android.content.Intent;
import android.util.Log;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.AppManager;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.ServiceManager;
import com.ipusoft.context.bean.Customer;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.sip.SipCacheApp;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.sip.constant.SipPhoneType;
import com.ipusoft.sip.service.SipCoreService;
import com.ipusoft.sip.service.SipPhoneFloatingService;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.StringUtils;

/**
 * author : GWFan
 * time   : 6/1/21 4:16 PM
 * desc   :
 */

public class SipFloatingViewIntentManager {
    private static final String TAG = "SipFloatingViewIntentMa";

    /**
     * 启动SIP呼叫的弹屏
     */
    public static void startSipCallOutFloatingService() {
        SipCallOutInfoBean sipCallOutInfoBean = SipCacheApp.getSipCallOutInfoBean();
        if (sipCallOutInfoBean == null) {
            sipCallOutInfoBean = new SipCallOutInfoBean();
        }
        SipPhoneType sipPhoneType = sipCallOutInfoBean.getSipPhoneType();
        if (sipPhoneType == SipPhoneType.CALL_IN) {
            String virtualNumber = SipCacheApp.getSipCallInVirtualNumber();
            String channel = SipCacheApp.getSipCallInChannel();
            Customer customer = SipCacheApp.getSipCallInCustomer();
            sipCallOutInfoBean.setVirtualNumber(virtualNumber);
            sipCallOutInfoBean.setChannel(channel);
            if (customer != null) {
                sipCallOutInfoBean.setName(customer.getName());
                // sipCallOutInfoBean.setPhoneArea();
                Integer sex = customer.getSex();
                if (sex != null) {
                    if (sex == 1) {
                        sipCallOutInfoBean.setSex("女");
                    } else if (sex == 2) {
                        sipCallOutInfoBean.setSex("男");
                    }
                }
                sipCallOutInfoBean.setSource(StringUtils.null2Empty(customer.getSource()));
                sipCallOutInfoBean.setSort(StringUtils.null2Empty(customer.getSort()));
                sipCallOutInfoBean.setStage(StringUtils.null2Empty(customer.getStage()));
            } else {
                sipCallOutInfoBean.setName("");
                sipCallOutInfoBean.setSource("");
                sipCallOutInfoBean.setStage("");
                sipCallOutInfoBean.setSort("");
                sipCallOutInfoBean.setSex("");
            }
        }

        if (ServiceManager.isServiceRunning(SipPhoneFloatingService.class)) {
            //Log.d(TAG, "startSipCallOutFloatingService: ===============1");
            Log.d(TAG, "startSipCallOutFloatingService: -----------1");
            LiveDataBus.get().with(LiveDataConstant.WINDOW_SHOW_SIP_CALL, SipCallOutInfoBean.class)
                    .postValue(sipCallOutInfoBean);
        } else {
            //Log.d(TAG, "startSipCallOutFloatingService: ===============2");
            Log.d(TAG, "startSipCallOutFloatingService: -----------2" + GsonUtils.toJson(sipCallOutInfoBean));
            Intent intent = new Intent(AppContext.getAppContext(), SipPhoneFloatingService.class);
            intent.setAction("com.ipusoft.siplibrary.service.SipCallOutFloatingService");
            intent.putExtra(LiveDataConstant.WINDOW_SHOW_SIP_CALL, GsonUtils.toJson(sipCallOutInfoBean));
            AppContext.getAppContext().startService(intent);
        }
        //}
    }

    public static void startSipHeartBeatService() {
        Log.d(TAG, "startSipHeartBeatService: ----------->1");
        if (ServiceManager.isServiceRunning(SipCoreService.class)) {
            LiveDataBus.get().with(LiveDataConstant.SIP_HEART_BEAT, Object.class)
                    .postValue(null);
        } else {
            //Log.d(TAG, "startSipCallOutFloatingService: ===============2");
            boolean runningForeground = AppManager.isRunningForeground(AppContext.getAppContext());
            if (runningForeground) {
                Intent intent = new Intent(AppContext.getAppContext(), SipCoreService.class);
                intent.setAction("com.ipusoft.siplibrary.service.SipCoreService");
                intent.putExtra(LiveDataConstant.SIP_HEART_BEAT, "");
                String token = AppContext.getToken();
                //if (StringUtils.isNotEmpty(token)) {
                AppContext.getAppContext().startService(intent);
                //}
            }
        }

//        boolean runningForeground = AppManager.isRunningForeground(AppContext.getAppContext());
//        boolean serviceRunning = ServiceManager.isServiceRunning(SipCoreService.class);
//        Log.d(TAG, "onCreate: .--------------1");
//        if (runningForeground && !serviceRunning) {
//            Log.d(TAG, "onCreate: .--------------2");
//            String token = AppContext.getToken();
//            if (StringUtils.isNotEmpty(token)) {
//                Log.d(TAG, "onCreate: .--------------3");
//                AppContext.getAppContext().startService(new Intent(AppContext.getAppContext(), SipCoreService.class));
//            }
//        }
    }
}
