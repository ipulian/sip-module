# SIP通讯SDK
**使用SIP通讯SDK之前，需要先集成 IpuSDK(https://github.com/ipulian/ipusdk)** 
有一些公共方法，在IpuSDK中已经做了说明，这里不再赘述。

**首先可以通过 ```java git clone https://github.com/ipulian/sip-module.git ``` 把该项目在Android Studio中直接运行。** 
## Setup
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
                     //使用时把 latest-version 替换成最新release版本
    implementation 'com.github.ipulian:sip-module:latest-version'
}
```
在AndroidManifest.xml中注册需要的权限
```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.USE_SIP" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
```
## Usage
- 1.通过SIP外呼(需要先申请android.permission.RECORD_AUDIO权限和 android.permission.SYSTEM_OVERLAY_WINDOW权限)
```java
SipPhoneManager.callPhoneBySip(phone);
//OR 带扩展字段外呼
SipPhoneManager.callPhoneBySip(phone,extendJson);
````
- 2.注册SIP通话状态的listener
```java
//继承 OnSipStatusChangedListener抽象类，并重写以下三个抽象方法 
public class OnSipStatusChangedListenerImpl extends OnSipStatusChangedListener{
    Override
    public  void onSipResponseError(SipResponse sipResponse){
        //TODO 外呼出错，通过sipResponse.getMsg()，可查看出错信息
    }
    Override  
    public abstract void onStartCall(){
        //TODO SIP准备完成(初始化，注册等)，开始外呼
    }
    Override
    public abstract void onEndCall(){
        //TODO 外呼结束，已经挂断
    }
}
```
并把该接口的实现类 OnSipStatusChangedListenerImpl 注册到 SDK中
```java
  //该方法在Application的onCreate中调用
 IpuSoftSDK.registerSipStatusChangedListener(new OnSipStatusChangedListenerImpl());
```
- 3.SIP暂不支持发送短信
- 4.获取通话状态,可以参考 IpuSDK(https://github.com/ipulian/ipusdk) 中的说明。
- 5.展示通话弹屏,可以参考 IpuSDK(https://github.com/ipulian/ipusdk) 中的说明。
- 6.如果APP退出登录，请同时退出SDK
```java
  //调用该方法之后，直到重新调用init()方法或者updateAuthInfo()方法，都不会再发送sip心跳)
 IpuSoftSDK.signOut();
```
## Tips
```
由于SIP SDK 使用的是UDP通信协议，所以在进行SIP通话的过程中，
务必保持手机屏幕常亮（Android手机在锁屏状态下，无法接收UDP数据包），否则将导致通话中断，直到重新点亮手机屏幕。
```
## ProGuard rules
```
-keep class com.ipusoft.sip.bean.** { *;}
```
# License
```
MIT License

Copyright (c) 2021 ipulian

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

