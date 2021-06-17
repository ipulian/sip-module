package com.ipusoft.sip.manager;

import android.content.Context;
import android.media.AudioManager;

/**
 * author : GWFan
 * time   : 1/29/21 2:09 PM
 * desc   :
 */

public class MyAudioManager {
    private static MyAudioManager instance;
    private static AudioManager audioManager;

    public static MyAudioManager getAudioManager(Context context) {
        if (instance == null) {
            synchronized (MyAudioManager.class) {
                if (instance == null) {
                    instance = new MyAudioManager();
                }
            }
        }
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        return instance;
    }

    /**
     * 切换到外放
     */
    public void changeToSpeaker() {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到耳机模式
     */
    public void changeToHeadset() {
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到听筒
     */
    public void changeToReceiver() {
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }
}
