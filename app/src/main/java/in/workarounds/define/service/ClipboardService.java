package in.workarounds.define.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.squareup.okhttp.internal.Util;

import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.portal.UtilPortlet;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalManager;
import in.workarounds.portal.Portlet;

public class ClipboardService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private static final String TAG = LogUtils.makeLogTag(ClipboardService.class);
    private static boolean isRunning = false;
<<<<<<< HEAD
    public static final String BUNDLE_SELECTED_TEXT_KEY = "BUNDLE_SELECTED_TEXT_KEY";
=======
    private Handler notificationHandler;
    private Runnable notificationHandlerRunnable;
>>>>>>> f9440bdbb1b12334b07174dee28b5f8fe6c26a7a

    @Override
    public void onCreate() {
        isRunning = true;
        ClipboardManager clipboardManager = getClipboardManager();
        clipboardManager.addPrimaryClipChangedListener(this);
        Portlet.with(this).id(UtilPortlet.UTIL_PORTLET_ID).open(UtilPortlet.class);
    }

    private ClipboardManager getClipboardManager() {
        return (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onPrimaryClipChanged() {
        String text = getClipData();
        if(!TextUtils.isEmpty(text)) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_SELECTED_TEXT_KEY,text);
            Portlet.with(this).id(UtilPortlet.UTIL_PORTLET_ID).data(bundle).send(UtilPortlet.class);
        }
    }

    private String getClipData() {
        ClipData clipData = getClipboardManager().getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        CharSequence text = item.getText();
        if (text != null && !URLUtil.isValidUrl(text.toString())) {
            return text.toString();
        } else {
            return null;
        }
    }
<<<<<<< HEAD
}
=======

    private void startActionResolver(String text) {
        int state = PortalManager.getPortalState(this, MainPortal.class);

        @UserPrefActivity.NotifyMode int notifyMode = PrefUtils.getNotifyMode(this);
        if(notifyMode == UserPrefActivity.OPTION_SILENT || notifyMode == UserPrefActivity.OPTION_PRIORITY) {
//Set notification priority as high for priority mode, default for silent mode
            int priority =  (notifyMode == UserPrefActivity.OPTION_PRIORITY)
                    ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT;

            NotificationUtils notificationUtils = new NotificationUtils(this);
            notificationUtils.sendMeaningNotification(text, priority);

            if(PrefUtils.getNotificationAutoHideFlag(this)) {
                notificationUtils.cancelBackupNotification();
                notificationHandler.removeCallbacks(notificationHandlerRunnable);
                notificationHandler.postDelayed(notificationHandlerRunnable, 10000);
            }
        }
        else{
            startPortal(text);
        }
    }

    private void startPortal(String text){
        Bundle bundle = new Bundle();
        bundle.putString(MainPortal.BUNDLE_KEY_CLIP_TEXT, text);
        Portal.with(this).data(bundle).send(MainPortal.class);
    }

    private void setNotificationClearer(){
        notificationHandlerRunnable =  new Runnable() {
            @Override
            public void run() {
                new NotificationUtils(ClipboardService.this).getNotificationManager().cancel(NotificationUtils.SILENT_NOTIFICATION_ID);
            }
        };
    }
}
>>>>>>> f9440bdbb1b12334b07174dee28b5f8fe6c26a7a
