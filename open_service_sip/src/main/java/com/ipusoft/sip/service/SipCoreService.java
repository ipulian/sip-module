package com.ipusoft.sip.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.ipusoft.context.AppContext;
import com.ipusoft.context.BaseLifeCycleService;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.bean.SeatInfo;
import com.ipusoft.context.bean.base.HttpResponse;
import com.ipusoft.context.constant.CallTypeConfig;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.logger.XLogger;
import com.ipusoft.mmkv.datastore.CommonDataRepo;
import com.ipusoft.sip.manager.SipManager;
import com.ipusoft.sip.module.SipService;
import com.ipusoft.utils.EncodeUtils;
import com.ipusoft.utils.ExceptionUtils;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.MD5Utils;
import com.ipusoft.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author : GWFan
 * time   : 2022/7/16 11:52
 * desc   :
 */

public class SipCoreService extends BaseLifeCycleService {
    private static final String TAG = "SipCoreService";

    private static final int CHECK_PERIOD = 30 * 1000;

    private volatile Timer mTimer;

    private static int status = 0;

    public static void setSipStatus(int sipStatus) {
        status = sipStatus;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onICreate() {
        XLogger.d("run: ------------>SipCoreService---->onICreate");
    }

    @Override
    protected void bindLiveData() {
        LiveDataBus.get().with(LiveDataConstant.SIP_HEART_BEAT, Object.class)
                .observe(this, o -> sipPing());
    }

    @Override
    protected void onIStartCommand(Intent intent, int flags, int startId) {
        //  String token = AppContext.getToken();
        // if (StringUtils.isNotEmpty(token)) {
        sipPing();
        //}
    }

    private void sipPing() {
        if (mTimer == null) {
            synchronized (SipCoreService.class) {
                if (mTimer == null) {
                    Log.d(TAG, "sipPing: .------------->1");
                    mTimer = new Timer();
                    mTimer.schedule(task, 5 * 1000, CHECK_PERIOD);
                }
            }
        }
    }

    private static int getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.parseInt(timestamp);
    }

    private static String getSign(String paramsJson) {
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        return MD5Utils.getMD5(paramsJson + seatInfo.getSdkSecret());
    }

    private static Map<String, Object> getMyParams() {
        HashMap<String, Object> params = new HashMap<>();
        String token = AppContext.getToken();
        String username = "";
        //      AuthInfo authInfo = AppContext.getAuthInfo();
        SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
        if (seatInfo != null) {
            username = seatInfo.getSeatNo();
        }
        params.put("token", token);
        params.put("status", status);
        params.put("seatId", username);
        params.put("ts", getSecondTimestamp(new Date()));//时间戳
        params.put("callType", "app");//时间戳
        return params;
    }

//    private static Map<String, Object> getParams(String phone) {
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("calledNo", phone);//被叫号码
//        AuthInfo authInfo = IpuSoftSDK.getAuthInfo();
//        String username = "";
//        if (authInfo != null) {
//            username = authInfo.getUsername();
//        }
//        params.put("callerId", username);//坐席编号
//        params.put("ts", getSecondTimestamp(new Date()));//时间戳
//        return params;
//    }

    private final TimerTask task = new TimerTask() {
        @Override
        public void run() {

            if (CommonDataRepo.getSipSDKSignOut()) {
                XLogger.d("----->SIP SDK 已退出登录");
                return;
            }

            new Thread(() -> {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //子线程调用login()方法之前，需要先调用libRegisterThread();
                Log.d(TAG, "js/call/ping: ---------------->" + Thread.currentThread().getName());
                SipManager.getInstance().libRegisterThread(Thread.currentThread().getName());
                SipManager.getInstance().login();
                SipManager.getInstance().resetLoginCnt();
            }).start();

            //主线程调用login()方法，一定几率会造成线程阻塞。
//            ThreadUtils.runOnUiThreadDelayed(() -> {
//                SipManager.getInstance().login();
//                SipManager.getInstance().resetLoginCnt();
//            }, 6 * 1000);

            String localCallType = CommonDataRepo.getLocalCallType();
            XLog.d("run: ------------>localCallType---->" + localCallType);
            //Log.d(TAG, "sipPing: .------------->" + localCallType);
            if (StringUtils.equals(CallTypeConfig.SIP.getType(), localCallType)) {
                SeatInfo seatInfo = CommonDataRepo.getSeatInfo();
                Log.d(TAG, "sipPing: .------------->" + GsonUtils.toJson(seatInfo));
                XLog.d("run: ------------>SipCoreService---->sipPing" + GsonUtils.toJson(seatInfo));
                if (seatInfo != null && StringUtils.isNotEmpty(seatInfo.getSeatNo())
                        && StringUtils.isNotEmpty(seatInfo.getSdkSecret())
                        && StringUtils.isNotEmpty(seatInfo.getApiKey())
                        && StringUtils.isNotEmpty(seatInfo.getPassword())) {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("api_key", seatInfo.getApiKey());
                    headers.put("sign", getSign(GsonUtils.toJson(getMyParams())));
                    // Map<String, Object> params = new HashMap<>();
                    // params.
                    Log.d(TAG, "run: .---------->" + getMyParams());
                    SipService.Companion.sipPing(headers, EncodeUtils.base64Encode2String(GsonUtils.toJson(getMyParams()).getBytes(StandardCharsets.UTF_8)), new Observer<HttpResponse>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull HttpResponse httpResponse) {
                            Log.d(TAG, "onNext: ---->" + GsonUtils.toJson(httpResponse));
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "onNext: ---->" + ExceptionUtils.getErrorInfo(e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                } else {
                    XLogger.d("sipPing失败,无坐席信息,seatInfo：" + GsonUtils.toJson(seatInfo));
                }
            }
        }
    };

    @Override
    protected void onIDestroy() {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
