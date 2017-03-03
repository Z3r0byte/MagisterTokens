package eu.z3r0byteapps.magistertokens.Util;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

import eu.z3r0byteapps.magistertokens.Container.List;
import eu.z3r0byteapps.magistertokens.ManageListsActivity;
import eu.z3r0byteapps.magistertokens.R;
import eu.z3r0byteapps.magistertokens.SettingsActivity;
import eu.z3r0byteapps.magistertokens.TokenActivity;

/**
 * Created by bas on 28-2-17.
 */

public class NavigationDrawer {
    private static final String TAG = "NavigationDrawer";

    Drawer drawer;

    AppCompatActivity activity;
    Toolbar toolbar;
    Boolean isTrial;
    Integer daysLeft;
    String selection;

    public NavigationDrawer(AppCompatActivity activity, Toolbar toolbar, Boolean isTrial, Integer daysLeft, String selection) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.isTrial = isTrial;
        this.daysLeft = daysLeft;
        this.selection = selection;
    }

    ArrayList<List> Lists;
    ArrayList<PrimaryDrawerItem> listItems = new ArrayList<>();

    //static SecondaryDrawerItem settingsItem = new SecondaryDrawerItem().withName(R.string.title_settings)
    //.withIcon(GoogleMaterial.Icon.gmd_settings).withSelectable(false);

    PrimaryDrawerItem manageListsItem = new PrimaryDrawerItem().withName(R.string.msg_manage_lists)
            .withIcon(GoogleMaterial.Icon.gmd_format_list_numbered);
    PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withName(R.string.msg_settings)
            .withIcon(GoogleMaterial.Icon.gmd_settings);


    public void setupNavigationDrawer() {
        generateDrawerItems();

        String license;
        if (isTrial) {
            license = String.format(activity.getString(R.string.msg_trial_left), daysLeft);
        } else {
            license = activity.getString(R.string.msg_premium);
        }

        final AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(activity.getString(R.string.msg_app_title)).withEmail(license)
                                .withIcon(R.drawable.logo)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        drawer.closeDrawer();
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .build();


        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withAccountHeader(accountHeader)
                .withActivity(activity)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == getSelection(selection)) {
                            drawer.closeDrawer();
                            return true;
                        }

                        if (drawerItem == manageListsItem) {
                            activity.startActivity(new Intent(activity, ManageListsActivity.class));
                        } else if (drawerItem == settingsItem) {
                            activity.startActivity(new Intent(activity, SettingsActivity.class));
                        } else {
                            activity.startActivity(new Intent(activity, TokenActivity.class).putExtra("listName", Lists.get(position - 1).getName()));
                        }
                        activity.finish();
                        return true;
                    }
                })
                .withSelectedItemByPosition(getSelection(selection));
        for (PrimaryDrawerItem item : listItems
                ) {
            drawerBuilder.addDrawerItems(item);
        }
        drawerBuilder.addDrawerItems(new DividerDrawerItem());
        drawerBuilder.addDrawerItems(manageListsItem, settingsItem);
        drawer = drawerBuilder.build();
    }

    private void generateDrawerItems() {
        ListDatabase listDatabase = new ListDatabase(activity);
        Lists = listDatabase.getLists();
        if (Lists == null || Lists.size() == 0) {
            return;
        }
        listItems.clear();
        for (List list : Lists
                ) {
            PrimaryDrawerItem item = new PrimaryDrawerItem().withName(list.getName()).withIcon(GoogleMaterial.Icon.gmd_list);
            listItems.add(item);
        }
    }

    private Integer getSelection(String selection) {
        if (selection == "manageLists") {
            return listItems.size() + 2;
        } else if (selection == "settings") {
            return listItems.size() + 3;
        } else {
            int i = 0;
            for (List list : Lists
                    ) {
                i++;
                if (list.getName().equals(selection)) {
                    return i;
                }
            }
        }
        return 0;
    }


}