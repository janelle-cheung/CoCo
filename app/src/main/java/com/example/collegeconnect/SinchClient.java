package com.example.collegeconnect;

import android.content.Context;
import android.content.Intent;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.video.VideoController;

import java.util.Map;

public class SinchClient implements com.sinch.android.rtc.SinchClient {

    public void instantiate(Context context, String userId) {
    }

    @Override
    public void setSupportMessaging(boolean b) {

    }

    @Override
    public void setSupportCalling(boolean b) {

    }

    @Override
    public void setSupportActiveConnectionInBackground(boolean b) {

    }

    @Override
    public void setSupportPushNotifications(boolean b) {

    }

    @Override
    public void setSupportManagedPush(boolean b) {

    }

    @Override
    public void checkManifest() {

    }

    @Override
    public CallClient getCallClient() {
        return null;
    }

    @Override
    public MessageClient getMessageClient() {
        return null;
    }

    @Override
    public void addSinchClientListener(SinchClientListener sinchClientListener) {

    }

    @Override
    public void removeSinchClientListener(SinchClientListener sinchClientListener) {

    }

    @Override
    public void start() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void terminate() {

    }

    @Override
    public void terminateGracefully() {

    }

    @Override
    public void startListeningOnActiveConnection() {

    }

    @Override
    public void stopListeningOnActiveConnection() {

    }

    @Override
    public void registerPushNotificationData(byte[] bytes) {

    }

    @Override
    public void unregisterPushNotificationData() {

    }

    @Override
    public void unregisterManagedPush() {

    }

    @Override
    public NotificationResult relayRemotePushNotificationPayload(String s) {
        return null;
    }

    @Override
    public NotificationResult relayRemotePushNotificationPayload(Intent intent) {
        return null;
    }

    @Override
    public NotificationResult relayRemotePushNotificationPayload(Map<String, String> map) {
        return null;
    }

    @Override
    public void setPushNotificationDisplayName(String s) {

    }

    @Override
    public void setMediaHandover(boolean b) {

    }

    @Override
    public String getLocalUserId() {
        return null;
    }

    @Override
    public AudioController getAudioController() {
        return null;
    }

    @Override
    public VideoController getVideoController() {
        return null;
    }
}
