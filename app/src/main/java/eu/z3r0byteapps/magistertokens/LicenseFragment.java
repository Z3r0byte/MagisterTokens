package eu.z3r0byteapps.magistertokens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.z3r0byteapps.magistertokens.Util.LicenseUtil;


public class LicenseFragment extends Fragment {
    View view;

    public LicenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_license, container, false);

        LicenseUtil.getLicense(getActivity());
        return view;
    }
}
