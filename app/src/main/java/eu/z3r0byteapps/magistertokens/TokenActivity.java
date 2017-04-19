package eu.z3r0byteapps.magistertokens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Date;

import eu.z3r0byteapps.magistertokens.Adapters.TokenListAdapter;
import eu.z3r0byteapps.magistertokens.Container.Token;
import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.DateUtils;
import eu.z3r0byteapps.magistertokens.Util.NavigationDrawer;
import eu.z3r0byteapps.magistertokens.Util.TokenDatabase;

public class TokenActivity extends AppCompatActivity {

    String listName;

    ListView listView;
    TokenDatabase tokenDatabase;
    TokenListAdapter tokenListAdapter;
    Token[] tokens;

    EditText searchField;
    RelativeLayout noResultsLayout;

    String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listName = getIntent().getStringExtra("listName");
        tokenDatabase = new TokenDatabase(this);

        setContentView(R.layout.activity_token);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(listName);
        toolbar.setSubtitle(tokenDatabase.getAmountOfTokens(listName) + getString(R.string.msg_amount_of_tokens));
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = searchField.getText().toString();
                updateTokens();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tokens = tokenDatabase.getTokens(query, listName);
        tokenListAdapter = new TokenListAdapter(this, tokens);
        listView = (ListView) findViewById(R.id.tokenList);
        listView.setAdapter(tokenListAdapter);

        ImageView noresultImageview = (ImageView) findViewById(R.id.no_results_icon);
        noresultImageview.setImageDrawable(
                new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_vpn_key)
                        .sizeDp(75)
                        .color(getResources().getColor(R.color.divider))
        );

        noResultsLayout = (RelativeLayout) findViewById(R.id.no_results_layout);

        ConfigUtil configUtil = new ConfigUtil(this);
        Boolean isTrial = configUtil.getBoolean("isTrial", true);
        Date endDate = DateUtils.parseDate(configUtil.getString("endDate", "2000-10-10 12:00"), "yyyy-MM-dd HH:mm");
        Integer daysLeft = DateUtils.diffDays(endDate, new Date());
        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, isTrial, daysLeft, listName);
        navigationDrawer.setupNavigationDrawer();
    }

    private void updateTokens() {
        tokens = null;
        tokens = tokenDatabase.getTokens(query, listName);
        if (tokens != null && tokens.length > 0) {
            tokenListAdapter = new TokenListAdapter(this, tokens);
            listView.setAdapter(tokenListAdapter);
            listView.setVisibility(View.VISIBLE);
            noResultsLayout.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }
}
