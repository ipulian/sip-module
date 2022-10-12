package com.ipusoft.sip.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.BaseLifeCycleService;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.bean.Customer;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.LiveDataConstant;
import com.ipusoft.context.constant.SipState;
import com.ipusoft.sip.ITimerTask;
import com.ipusoft.sip.SipCacheApp;
import com.ipusoft.sip.adapter.SipPhoneFloatingViewAdapter;
import com.ipusoft.sip.bean.SipCallOutInfoBean;
import com.ipusoft.sip.constant.CallStatusCode;
import com.ipusoft.sip.constant.SipPhoneType;
import com.ipusoft.sip.iface.OnSipCallOutWindowClickListener;
import com.ipusoft.sip.manager.MediaPlayerManager;
import com.ipusoft.sip.manager.SipPhoneManager;
import com.ipusoft.sip.view.SipMiniFloatingView;
import com.ipusoft.sip.view.SipPhoneFloatingView;
import com.ipusoft.utils.GsonUtils;
import com.ipusoft.utils.StringUtils;
import com.ipusoft.utils.ThreadUtils;

import java.util.TimerTask;

/**
 * author : GWFan
 * time   : 5/31/21 6:14 PM
 * desc   :
 */
public class SipPhoneFloatingService extends BaseLifeCycleService implements OnSipCallOutWindowClickListener {
    private static final String TAG = "SipCallOutFloatingServi";
    /**
     * SIP外呼视图
     */
    private SipPhoneFloatingView mFloatingView;

    private SipMiniFloatingView sipMiniFloatingView;

    private SipPhoneFloatingViewAdapter sipAdapter;

    private int i = 0;
    private ITimerTask task;

    private SipCallOutInfoBean sipCallOutBean;

    @Override
    protected void onICreate() {
        mFloatingView = new SipPhoneFloatingView(this);
        mFloatingView.setOnClickListener(this);
        sipAdapter = new SipPhoneFloatingViewAdapter();
        mFloatingView.setAdapter(sipAdapter);

        sipMiniFloatingView = new SipMiniFloatingView(this);

        try {
            if (AppContext.getActivityContext() != null) {
                AppContext.getActivityContext().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            @SuppressLint("InvalidWakeLockTag")
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
            wakeLock.acquire(100 * 60 * 1000L /*100 minutes*/);
            //wakeLock.release();
        }

        sipMiniFloatingView.setOnClickListener(v -> {
            sipAdapter.updateData(sipCallOutBean);
            mFloatingView.show(sipCallOutBean.getSipPhoneType());
            sipMiniFloatingView.dismiss();
        });
    }

    @Override
    protected void bindLiveData() {
        LiveDataBus.get().with(LiveDataConstant.WINDOW_SHOW_SIP_CALL, SipCallOutInfoBean.class)
                .observe(this, sipBean -> {
                    //Log.d(TAG, "onIStartCommand: ----------->2");
                    this.sipCallOutBean = sipBean;
                    sipAdapter.updateData(sipBean);
                    mFloatingView.show(sipBean.getSipPhoneType());
                });

        LiveDataBus.get().with(LiveDataConstant.REFRESH_SIP_WINDOW, Object.class).observe(this, o -> {
            Log.d(TAG, "bindLiveData: ---------->");
            SipCallOutInfoBean sipCallOutInfoBean = SipCacheApp.getSipCallOutInfoBean();
            if (sipCallOutInfoBean != null) {
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
                    if (sipAdapter != null) {
                        sipAdapter.updateData(sipCallOutInfoBean);
                    }
                }
            }
        });

        LiveDataBus.get().with(LiveDataConstant.UPDATE_SIP_CALL_STATUS, SipState.class)
                .observe(this, sipCallStatus -> {
                    Log.d(TAG, "bindLiveData: ----------:" + sipCallStatus);
                    ToastUtils.dismiss();
                    if (CallStatusCode.CODE_1 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.playCallOutRing(AppContext.getAppContext());
                    } else if (CallStatusCode.CODE_2 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.playCallOutRing(AppContext.getAppContext());
                    } else if (CallStatusCode.CODE_3 == sipCallStatus.getStatus()) {
                        MediaPlayerManager.stopAndReleasePlay();
                    } else if (CallStatusCode.CODE_4 == sipCallStatus.getStatus()) {
                        /*
                         * 部分线路，没有CallStatusCode.CODE_3
                         */
                        MediaPlayerManager.stopAndReleasePlay();
                    } else if (CallStatusCode.CODE_5 == sipCallStatus.getStatus()) {
                        try {

                            MediaPlayerManager.stopAndReleasePlay();

                            i = 0;
                            task = new ITimerTask(1000, 1000, new TimerTask() {
                                @Override
                                public void run() {
                                    ThreadUtils.runOnUiThread(() -> sipAdapter.updateSipCallDuration(++i));
                                }
                            });
                            task.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (CallStatusCode.CODE_6 == sipCallStatus.getStatus()) {
                        try {
                            if (task != null) {
                                task.stop();
                            }
                            MediaPlayerManager.playHungUpRing(AppContext.getAppContext(),
                                    mp -> {
                                        mFloatingView.dismiss();
                                        sipMiniFloatingView.dismiss();
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (CallStatusCode.CODE_5 != sipCallStatus.getStatus()) {
                        sipAdapter.updateSipStatus(sipCallStatus);
                    }
                    Log.d(TAG, "onIStartCommand: ----------->3--->" + sipCallStatus);
                });
    }

    @Override
    protected void onIStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String json = intent.getStringExtra(LiveDataConstant.WINDOW_SHOW_SIP_CALL);
            if (StringUtils.isNotEmpty(json)) {
                SipCallOutInfoBean sipCallOutInfoBean = GsonUtils.fromJson(json, SipCallOutInfoBean.class);
                Log.d(TAG, "onIStartCommand: ----------->1");
                this.sipCallOutBean = sipCallOutInfoBean;
                sipAdapter.updateData(sipCallOutInfoBean);
                mFloatingView.show(sipCallOutInfoBean.getSipPhoneType());
            }
        }
    }

    @Override
    protected void onIDestroy() {
        SipCacheApp.setSIPCallOutBean(null);
        SipCacheApp.setSipCallInNumberInfo("", "", null);
        if (mFloatingView != null) {
            mFloatingView.dismiss();
            sipMiniFloatingView.dismiss();
            mFloatingView = null;
        }
    }

    @Override
    public void onDismiss() {
        //SipCacheApp.setSipCallOutInfoBean();
        SipCacheApp.setSIPCallOutBean(null);
        SipCacheApp.setSipCallInNumberInfo("", "", null);
        if (mFloatingView != null) {
            mFloatingView.dismiss();
            sipMiniFloatingView.dismiss();
        }
    }

    @Override
    public void hungUp() {
        Log.d(TAG, "hungUp: --------------->111111");
        SipCacheApp.setSIPCallOutBean(null);
        SipCacheApp.setSipCallInNumberInfo("", "", null);
        SipPhoneManager.dropPhone();
        if (task != null) {
            task.stop();
        }
    }

    @Override
    public void dialDTMF(String number) {
        SipPhoneManager.dialDTMF(number);
    }

    @Override
    public void answerCall() {
        SipPhoneManager.answerCall();
    }

    @Override
    public void miniWindow() {
        sipMiniFloatingView.show();
        // mWindowManager.addView(getMiniView(), mLayoutParams);
    }

//    private View getMiniView() {
//        return LayoutInflater.from(this).inflate(R.layout.layout_mini_view, null);
//    }
//
//    public static WindowManager.LayoutParams getWrapLayoutParams() {
//        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
//                    | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
//        }
//        mLayoutParams.format = PixelFormat.RGBA_8888;
//        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
//        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        mLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//        mLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        return mLayoutParams;
//    }

}
