package eu.z3r0byteapps.magistertokens;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import eu.z3r0byteapps.magistertokens.Container.License;
import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.DateUtils;
import eu.z3r0byteapps.magistertokens.Util.LicenseUtil;
import eu.z3r0byteapps.magistertokens.Util.NavigationDrawer;

public class LicenseActivity extends AppCompatActivity {
    private static final String TAG = "LicenseActivity";

    TextView status;
    ProgressBar progressBar;
    TextView buyLicense;
    TextView refreshLicense;
    TextView infoLicense;

    ConfigUtil configUtil;


    final static String SKU_FULL_LICENSE = "pro_license";

    IInAppBillingService mService;
    Bundle ownedItems;
    ArrayList<String> boughtSKU = new ArrayList<>();
    ArrayList<String> boughtToken = new ArrayList<>();

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    Bundle querySkus = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        status = (TextView) findViewById(R.id.status);
        progressBar = (ProgressBar) findViewById(R.id.daysLeftProgressBar);
        configUtil = new ConfigUtil(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.msg_license);
        setSupportActionBar(toolbar);

        Boolean isTrial = configUtil.getBoolean("isTrial", true);
        Date endDate = DateUtils.parseDate(configUtil.getString("endDate", "2000-10-10 12:00"), "yyyy-MM-dd HH:mm");
        Integer daysLeft = DateUtils.diffDays(endDate, new Date());
        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, isTrial, daysLeft, "license");
        navigationDrawer.setupNavigationDrawer();

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(SKU_FULL_LICENSE);
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        buyLicense = (TextView) findViewById(R.id.buy_license);
        refreshLicense = (TextView) findViewById(R.id.refresh_license);
        infoLicense = (TextView) findViewById(R.id.info_licenses);


        buyLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchase(SKU_FULL_LICENSE);
            }
        });
        refreshLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPurchases();
            }
        });
        infoLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                licenseInfo();
            }
        });

        updateInterface();
    }

    private void licenseInfo() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.title_license_info)
                .content(Html.fromHtml(getString(R.string.body_license_info)))
                .positiveText(R.string.msg_okay)
                .build();
        dialog.show();
    }

    private void purchase(final String SKU) {
        if (!boughtSKU.contains(SKU)) {
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                        SKU, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Licentie al gekocht!", Toast.LENGTH_SHORT).show();
        }
    }


    private void getPurchases() {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                getString(R.string.msg_loading_purchases), true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                License license = LicenseUtil.getLicense(getApplicationContext());
                configUtil.setString("endDate", license.endDate);
                configUtil.setBoolean("isValid", license.valid);
                configUtil.setBoolean("isTrial", license.isTrial);

                Looper.prepare();
                boughtSKU.clear();
                boughtToken.clear();
                try {
                    Thread.sleep(500);
                    ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);

                    int response = ownedItems.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> ownedSkus =
                                ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                        ArrayList<String> purchaseDataList =
                                ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                        ArrayList<String> signatureList =
                                ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");


                        for (int i = 0; i < purchaseDataList.size(); ++i) {
                            String purchaseData = purchaseDataList.get(i);
                            String signature = signatureList.get(i);
                            String sku = ownedSkus.get(i);

                            JSONObject jo = new JSONObject(purchaseData);
                            String token = jo.getString("purchaseToken");

                            boughtSKU.add(sku);
                            boughtToken.add(token);

                            Log.i(TAG, "run: Purchased item " + i + ": SKU: " + sku +
                                    ", purchaseData:" + purchaseData + ", Signature: " + signature);

                            if (boughtSKU.contains(SKU_FULL_LICENSE)) {
                                configUtil.setBoolean("isTrial", false);
                                configUtil.setBoolean("isValid", true);
                            }
                        }
                    }

                } catch (RemoteException e) {
                    if (mService != null) {
                        unbindService(mServiceConn);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LicenseActivity.this, R.string.err_no_connection, Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    finish();
                } catch (InterruptedException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LicenseActivity.this, R.string.err_unknown, Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    finish();
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LicenseActivity.this, R.string.err_unknown, Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateInterface();
                    }
                });

                dialog.dismiss();
            }
        }).start();
    }

    private void updateInterface() {
        if (configUtil.getBoolean("isTrial", true)) {
            if (configUtil.getBoolean("isValid", false)) {
                Date endDate = DateUtils.parseDate(configUtil.getString("endDate", "2000-10-10 12:00"), "yyyy-MM-dd HH:mm");
                Integer daysLeft = DateUtils.diffDays(endDate, new Date());
                progressBar.setProgress(daysLeft);
                status.setText(String.format(getString(R.string.trial_days_left), daysLeft));
            } else {
                status.setText(R.string.err_license_invalid);
                progressBar.setProgress(0);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            status.setText(R.string.msg_premium);
        }
        buyLicense.setEnabled(configUtil.getBoolean("isTrial", true));
    }
}
