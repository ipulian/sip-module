package com.ipusoft.siplibrary.manager;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;

import com.ipusoft.context.AppContext;
import com.ipusoft.siplibrary.constant.AudioPlayType;

/**
 * author : GWFan
 * time   : 2/1/21 3:10 PM
 * desc   :
 */

public class HardWareManager {

    /**
     * 检测是否连接耳机或者蓝牙耳机
     *
     * @return
     */
    public static AudioPlayType checkOutPutDeviceType() {
        AudioManager audioManager = (AudioManager) AppContext.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                int deviceType = device.getType();
                if (deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return AudioPlayType.BLUETOOTH;
                }
                if (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                    return AudioPlayType.HEADSET;
                }
            }
        } else {
            if (audioManager.isBluetoothScoOn() || audioManager.isBluetoothA2dpOn()) {
                return AudioPlayType.BLUETOOTH;
            }
            if (audioManager.isWiredHeadsetOn()) {
                return AudioPlayType.HEADSET;
            }
        }
        return AudioPlayType.SPEAKER;
    }


    public static boolean checkHeadsetPlug() {
        AudioManager audioManager = (AudioManager) AppContext.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                int deviceType = device.getType();
                if (deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                        || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
                    return true;
                }
            }
        } else {
            return audioManager.isBluetoothScoOn() || audioManager.isBluetoothA2dpOn() || audioManager.isWiredHeadsetOn();
        }
        return false;
    }
}
