package eu.z3r0byteapps.magistertokens;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

import java.util.Date;

import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.DateUtils;
import eu.z3r0byteapps.magistertokens.Util.NavigationDrawer;

public class SettingsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ConfigUtil configUtil = new ConfigUtil(this);
        Boolean isTrial = configUtil.getBoolean("isTrial", true);
        Date endDate = DateUtils.parseDate(configUtil.getString("endDate", "2000-10-10 12:00"), "yyyy-MM-dd HH:mm");
        Integer daysLeft = DateUtils.diffDays(endDate, new Date());
        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, isTrial, daysLeft, "settings");
        navigationDrawer.setupNavigationDrawer();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SecurityFragment();
                case 1:
                    LibsSupportFragment libsFragment = new LibsBuilder()
                            .supportFragment();
                    return libsFragment;
                default:
                    return new LicenseFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.msg_security);
                case 1:
                    return getString(R.string.msg_about);
            }
            return null;
        }
    }
}
