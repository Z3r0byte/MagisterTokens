package eu.z3r0byteapps.magistertokens.Util;

import android.content.Context;
import android.os.HandlerThread;
import android.provider.Settings;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import eu.z3r0byteapps.magistertokens.Container.License;

/**
 * Created by bas on 3-3-17.
 */

public class LicenseUtil {

    public static License getLicense(final Context context) {
        if (hasPurchased(context)) {
            return new License(false, true, DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
        }

        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final License[] license = new License[1];
            Thread uiThread = new HandlerThread("TrialHandler") {
                @Override
                public void run() {
                    try {
                        String response = HttpUtil.convertInputStreamReaderToString(
                                HttpUtil.httpGet("https://api.z3r0byteapps.eu/license/magistertokens/license.php?id="
                                        + getUniqueId(context)));
                        if (response.contains("error")) {
                            license[0] = new License(true, false, DateUtils.formatDate(
                                    new Date(), "yyyy-MM-dd HH:mm"));
                        } else {
                            license[0] = new Gson().fromJson(response, License.class);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        license[0] = new License(true, true, DateUtils.formatDate(
                                DateUtils.addHours(new Date(), 24), "yyyy-MM-dd HH:mm"));
                    }
                    latch.countDown();
                }
            };
            uiThread.start();
            latch.await();
            return license[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new License(true, true, DateUtils.formatDate(
                    DateUtils.addHours(new Date(), 24), "yyyy-MM-dd HH:mm"));
        }

    }


    private static String getUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static Boolean hasPurchased(Context context) {
        ConfigUtil configUtil = new ConfigUtil(context);
        return !configUtil.getBoolean("isTrial", true);
    }
}
