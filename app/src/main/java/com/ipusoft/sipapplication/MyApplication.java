package com.ipusoft.sipapplication;

import android.app.Application;
import android.widget.Toast;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.bean.AuthInfo;
import com.ipusoft.context.bean.SipResponse;
import com.ipusoft.context.config.Env;
import com.ipusoft.sip.ifaceimpl.OnSipStatusChangedListener;

/**
 * @author : GWFan
 * time   : 2022/9/21 16:21
 * desc   :
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化，注册

        //生产环境
//        IpuSoftSDK.init(this, Env.OPEN_PRO);
//        IpuSoftSDK.updateAuthInfo(new AuthInfo("4822691987324969", "5e3b5ef6bd877e6f4e631e52c5b5af8b",
//                "00002", "ccc51bd1b3ae442d9548b66d4bfd61e4"));

        //开发环境
        IpuSoftSDK.init(this, Env.OPEN_DEV);
        IpuSoftSDK.updateAuthInfo(new AuthInfo("4815437930168322", "4f244a0d86a99663e8aeb0849b3513c3", "00002", "53e891b5fa7c46fa9b60d0e440f57d9f"));


//        IpuSoftSDK.updateAuthInfo(new AuthInfo(
//                "4815437930168322",
//                "db52c2c9c256857e16cf7ab02ba51210",
//                "1001", "xxxxxx"));

        //实现SIP状态listener(非必须)
        IpuSoftSDK.registerSipStatusChangedListener(new OnSipStatusChangedListenerImpl());
    }
}

//继承 OnSipStatusChangedListener抽象类，并重写以下三个抽象方法，如果想获取更加详细的通话状态，可以继承 BaseSipStatusChangedListener
//详情参考 OnSipStatusChangedListener.java
class OnSipStatusChangedListenerImpl extends OnSipStatusChangedListener {
    private static final String TAG = "MyApplication";

    @Override
    public void onSipResponseError(SipResponse sipResponse) {

    }

    @Override
    public void onStartCall() {
        Toast.makeText(AppContext.getActivityContext(), "通话已开始", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEndCall() {
        //  Toast.makeText(AppContext.getActivityContext(), "通话已结束", Toast.LENGTH_SHORT).show();
    }
}
