package eu.z3r0byteapps.magistertokens;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import java.util.Date;

import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.DateUtils;


public class LicenseFragment extends Fragment {
    View view;

    TextView buyLicense;
    TextView refreshLicense;
    TextView infoLicense;

    public LicenseFragment() {
        // Required empty public constructor
    }

    IInAppBillingService mService;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_license, container, false);

        TextView status = (TextView) view.findViewById(R.id.status);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.daysLeftProgressBar);
        ConfigUtil configUtil = new ConfigUtil(getActivity());

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

        buyLicense = (TextView) view.findViewById(R.id.buy_license);
        refreshLicense = (TextView) view.findViewById(R.id.refresh_license);
        infoLicense = (TextView) view.findViewById(R.id.info_licenses);


        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            getActivity().unbindService(mServiceConn);
        }
    }
}
