package eu.z3r0byteapps.magistertokens.Util;

import android.content.Context;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import eu.z3r0byteapps.magistertokens.Container.License;

/**
 * Created by bas on 3-3-17.
 */

public class LicenseUtil {
    private static final String TAG = "LicenseUtil";

    public static License getLicense(Context context) {

        Log.d(TAG, "getLicense: Unique Id: " + getUniqueId(context));
        Log.d(TAG, "getLicense: Trial started: " + isTrialStarted(context));
        ConfigUtil configUtil = new ConfigUtil(context);

        if (configUtil.getInteger("daysLeft", 99) == 99) {
            try {
                startTrial();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
        /*String licenseStr;
        try {
            licenseStr = HttpUtil.convertInputStreamReaderToString(
                    HttpUtil.httpGet("https://api.z3r0byteapps.eu/license/magistertokens/check.php?id=" + getUniqueId(context)));
        } catch (IOException e){
            e.printStackTrace();
        }
        */

    }

    public static void startTrial() throws IOException {

    }

    public static Boolean isTrialStarted(final Context context) {
        ConfigUtil configUtil = new ConfigUtil(context);
        if (configUtil.getBoolean("trialStarted", false)) {
            return true;
        } else {
            try {
                final CountDownLatch latch = new CountDownLatch(1);
                final Boolean[] isStarted = new Boolean[1];
                Thread uiThread = new HandlerThread("TrialHandler") {
                    @Override
                    public void run() {
                        try {
                            String response = HttpUtil.convertInputStreamReaderToString(
                                    HttpUtil.httpGet("https://api.z3r0byteapps.eu/license/magistertokens/trialstarted.php?id="
                                            + getUniqueId(context)));
                            if (response.contains("true")) {
                                isStarted[0] = true;
                            } else {
                                isStarted[0] = false;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            isStarted[0] = false;
                        }
                        latch.countDown();
                    }
                };
                uiThread.start();
                latch.await();
                return isStarted[0];
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

        }
    }


    private static String getUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
