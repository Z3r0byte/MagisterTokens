package eu.z3r0byteapps.magistertokens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.DateUtils;


public class LicenseFragment extends Fragment {
    View view;

    TextView buyLicense;
    TextView refreshLicense;
    TextView infoLicense;


    final static String SKU_FULL_LICENSE = "pro_license";



    public LicenseFragment() {
        // Required empty public constructor
    }



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


        buyLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchase(SKU_FULL_LICENSE);
            }
        });



        return view;
    }

    private void purchase(final String SKU) {
        /*if (!boughtSKU.contains(SKU)) {
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(),
                        SKU, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        1001, new Intent(getActivity()), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.dialog_item_purchased_title));
            alertDialogBuilder.setMessage(getString(R.string.dialog_item_purchased_desc));
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    consumePurchase(SKU);
                }
            });
            alertDialogBuilder.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }*/
    }
}
