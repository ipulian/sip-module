package com.ipusoft.siplibrary.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.RawRes;

import com.ipusoft.siplibrary.R;

import java.io.IOException;

/**
 * author : GWFan
 * time   : 3/18/21 10:12 AM
 * desc   : 多媒体管理类
 */

public class MediaPlayerManager {
    private static final String TAG = "MediaPlayerManager";
    private static volatile MediaPlayer mediaPlayer;

    private MediaPlayerManager() {
    }

    public static MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            synchronized (MediaPlayerManager.class) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
            }
        }
        return mediaPlayer;
    }

    public interface OnVoiceCompletionListener {
        void onCompletion(MediaPlayer mp);
    }

    /**
     * 播放系统声音
     *
     * @param context
     * @param resId
     */
    public static void playSystemVoice(Context context, @RawRes int resId, boolean isLooping, OnVoiceCompletionListener listener) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
            mediaPlayer = MediaPlayerManager.getMediaPlayer();
            String pkgName = context.getPackageName();
            //  Log.d(TAG, "playSystemVoice: ------>" + pkgName);
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + pkgName + "/" + resId));
            mediaPlayer.setLooping(isLooping);
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (listener != null) {
                mediaPlayer.setOnCompletionListener(listener::onCompletion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放外呼等待提示音
     */
    public static void playCallOutRing(Context context) {
        playSystemVoice(context, R.raw.du_callout, true, null);
    }

    /**
     * 播放挂断提示音
     */
    public static void playHungUpRing(Context context, OnVoiceCompletionListener listener) {
        playSystemVoice(context, R.raw.du_hungup, false, mp -> {
            stopAndReleasePlay();
            if (listener != null) {
                listener.onCompletion(mp);
            }
        });
    }

    /**
     * 根据按键类型播放提示音
     *
     * @param context
     */
    public static void playPressTipVoice(Context context, @RawRes int rawId) {
        if (rawId != 0) {
            playSystemVoice(context, rawId, false, null);
        }
    }

    /**
     * 停止播放
     */
    public static void stopAndReleasePlay() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            //LogUtils.d("stopPlay:" + e.toString());
        }
    }
}
