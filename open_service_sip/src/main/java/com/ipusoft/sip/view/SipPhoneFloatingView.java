package com.ipusoft.sip.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

//import com.gyf.immersionbar.ImmersionBar;
import com.ipusoft.context.AppContext;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.manager.IWindowManager;
import com.ipusoft.permission.RxPermissionUtils;
import com.ipusoft.sip.R;
import com.ipusoft.sip.adapter.SipPhoneFloatingViewAdapter;
import com.ipusoft.sip.constant.AudioPlayType;
import com.ipusoft.sip.constant.SipPhoneType;
import com.ipusoft.sip.iface.OnHeadsetPlugListener;
import com.ipusoft.sip.iface.OnSipCallOutWindowClickListener;
import com.ipusoft.sip.iface.OnSipWindowDialPanClickListener;
import com.ipusoft.sip.ifaceimpl.OnHeadsetPlugListenerImpl;
import com.ipusoft.sip.manager.HardWareManager;
import com.ipusoft.sip.manager.MyAudioManager;
import com.ipusoft.utils.DeviceUtils;
import com.ipusoft.utils.ResourceUtils;
import com.ipusoft.utils.SizeUtils;
import com.ipusoft.utils.StringUtils;

/**
 * author : GWFan
 * time   : 5/31/21 6:24 PM
 * desc   :
 */

public class SipPhoneFloatingView extends LinearLayout implements View.OnClickListener,
        OnSipWindowDialPanClickListener, OnHeadsetPlugListener {
    private OnSipCallOutWindowClickListener listener;
    private View view;
    private WindowManager mWindowManager;
    private boolean mIsShow;
    private WindowManager.LayoutParams mLayoutParams;
    private boolean showDialPan = false;
    private LinearLayout llDialPanRoot, llInfo;
    private TextView tvInput;
    private ImageView ivArrow, ivAudioMode;
    private boolean flag = true;


    public SipPhoneFloatingView(Context context) {
        super(context);
        initView(context);
    }

    public SipPhoneFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initWindow() {
        mWindowManager = IWindowManager.getWindowManager();
        getWindowParams();
    }

    public void getWindowParams() {
        if (mLayoutParams == null) {
            synchronized (IWindowManager.class) {
                if (mLayoutParams == null) {
                    mLayoutParams = new WindowManager.LayoutParams();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                                | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                    }
                    mLayoutParams.y = 0;
                    mLayoutParams.format = PixelFormat.RGBA_8888;
                    mLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    mLayoutParams.gravity = Gravity.TOP;
                    mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                }
            }
        }
    }

    private void initView(Context context) {
        initWindow();
        showDialPan = false;
        flag = true;

        view = LayoutInflater.from(context).inflate(R.layout.sip_call_out_layout, this);

        llInfo = view.findViewById(R.id.ll_info);
        llDialPanRoot = view.findViewById(R.id.ll_dial_pan_root);
        tvInput = view.findViewById(R.id.tv_input);
        LinearLayout llDialPan = view.findViewById(R.id.ll_dial_pan);

        llDialPan.removeAllViews();
        SipWindowDialPanView sipWindowDialPanView = new SipWindowDialPanView(context);
        sipWindowDialPanView.setOnSipDialPanClickListener(this);
        llDialPan.addView(sipWindowDialPanView);

        view.findViewById(R.id.iv_hung_up).setOnClickListener(this);
        view.findViewById(R.id.iv_dial_pan_show).setOnClickListener(this);
        view.findViewById(R.id.iv_audio_mode).setOnClickListener(this);
        view.findViewById(R.id.iv_arrow).setOnClickListener(this);
        view.findViewById(R.id.iv_receive).setOnClickListener(this);

        ivArrow = view.findViewById(R.id.iv_arrow);
        ivAudioMode = view.findViewById(R.id.iv_audio_mode);
        ivAudioMode.setOnClickListener(this);
        /*
         * 设置耳机播放还是听筒播放
         */
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(45), SizeUtils.dp2px(45));
        AudioPlayType audioPlayType = HardWareManager.checkOutPutDeviceType();
        if (audioPlayType == AudioPlayType.BLUETOOTH || audioPlayType == AudioPlayType.HEADSET) {
            MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToHeadset();
            ivArrow.setVisibility(View.VISIBLE);
            ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_headset));
            ivAudioMode.setLayoutParams(layoutParams);
        } else {
            MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToReceiver();
            ivArrow.setVisibility(View.GONE);
            ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_receiver));
        }
    }

    public void setOnClickListener(OnSipCallOutWindowClickListener listener) {
        this.listener = listener;
    }

    public void show(SipPhoneType sipPhoneType) {
        try {
            boolean b = RxPermissionUtils.hasOverLayPermission(AppContext.getActivityContext());
            if (b) {
                if (!mIsShow) {
                    mIsShow = true;
                    updateViewBySipPhoneType(sipPhoneType);
                    mWindowManager.addView(view, mLayoutParams);
                }
//                ImmersionBar.with(AppContext.getActivityContext())
//                        .autoDarkModeEnable(true)
//                        .autoStatusBarDarkModeEnable(true)
//                        .transparentStatusBar()
//                        .statusBarColor(R.color.dial_bg)
//                        .init();
                OnHeadsetPlugListenerImpl.setOnHeadsetPlugListener(this);
            } else {
                ToastUtils.showMessage("请打开悬浮窗权限");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateViewBySipPhoneType(SipPhoneType sipPhoneType) {
        if (sipPhoneType == SipPhoneType.CALL_OUT || sipPhoneType == SipPhoneType.CALL_IN_ANSWERED) {
            view.findViewById(R.id.ll_receive).setVisibility(GONE);
            view.findViewById(R.id.ll_dial_pan_show).setVisibility(VISIBLE);
            view.findViewById(R.id.ll_audio_mode).setVisibility(VISIBLE);
        } else {
            view.findViewById(R.id.ll_dial_pan_show).setVisibility(GONE);
            view.findViewById(R.id.ll_audio_mode).setVisibility(GONE);
            view.findViewById(R.id.ll_receive).setVisibility(VISIBLE);
        }
    }

    public void dismiss() {
        boolean b = RxPermissionUtils.hasOverLayPermission(AppContext.getActivityContext());
        if (b) {
            if (mIsShow) {
                mIsShow = false;
                mWindowManager.removeViewImmediate(view);
//                ImmersionBar.with(AppContext.getActivityContext())
//                        .autoDarkModeEnable(true)
//                        .autoStatusBarDarkModeEnable(true)
//                        .transparentStatusBar()
//                        .statusBarColor(R.color.themeColor)
//                        .init();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_hung_up) {
            if (listener != null) {
                DeviceUtils.vibrate(30);
                dismiss();
                listener.hungUp();
            }
        } else if (v.getId() == R.id.iv_receive) {
            DeviceUtils.vibrate(30);
            updateViewBySipPhoneType(SipPhoneType.CALL_IN_ANSWERED);
            listener.answerCall();
        } else if (v.getId() == R.id.iv_dial_pan_show) {
            DeviceUtils.vibrate(30);
            if (showDialPan) {
                llDialPanRoot.setVisibility(View.GONE);
                llInfo.setVisibility(View.VISIBLE);
                tvInput.setText("");
            } else {
                llInfo.setVisibility(View.GONE);
                llDialPanRoot.setVisibility(View.VISIBLE);
            }
            showDialPan = !showDialPan;
        } else if (v.getId() == R.id.iv_audio_mode) {
            DeviceUtils.vibrate(30);
            boolean hasHeadset = HardWareManager.checkHeadsetPlug();
            if (hasHeadset) {
                ivArrow.setVisibility(View.VISIBLE);
                if (flag) {
                    ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_speaker));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(62), SizeUtils.dp2px(62));
                    ivAudioMode.setLayoutParams(layoutParams);
                    flag = false;
                    MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToSpeaker();
                } else {
                    ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_headset));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(45), SizeUtils.dp2px(45));
                    ivAudioMode.setLayoutParams(layoutParams);
                    flag = true;
                    MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToHeadset();
                }
            } else {
                ivArrow.setVisibility(View.GONE);
                if (flag) {
                    ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_speaker));
                    MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToSpeaker();
                    flag = false;
                } else {
                    ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_receiver));
                    MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToReceiver();
                    flag = true;
                }
            }
        }
    }

    public void setAdapter(SipPhoneFloatingViewAdapter mAdapter) {
        mAdapter.updateView(this);
    }

    @Override
    public void onSipDialPanClick(String input) {
        String str = StringUtils.null2Empty(tvInput.getText().toString()) + input;
        tvInput.setText(str);
        if (listener != null) {
            listener.dialDTMF(input);
        }
    }


    @Override
    public void onHeadsetPlug(AudioPlayType audioPlayType) {
        if (audioPlayType == AudioPlayType.BLUETOOTH || audioPlayType == AudioPlayType.HEADSET) {
            MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToHeadset();
            ivArrow.setVisibility(View.VISIBLE);
            ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_headset));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(45), SizeUtils.dp2px(45));
            ivAudioMode.setLayoutParams(layoutParams);
        } else if (audioPlayType == AudioPlayType.SPEAKER) {
            MyAudioManager.getAudioManager(AppContext.getAppContext()).changeToSpeaker();
            ivArrow.setVisibility(View.GONE);
            ivAudioMode.setImageDrawable(ResourceUtils.getDrawable(R.drawable.sip_ic_speaker));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SizeUtils.dp2px(62), SizeUtils.dp2px(62));
            ivAudioMode.setLayoutParams(layoutParams);
        }
    }
}
