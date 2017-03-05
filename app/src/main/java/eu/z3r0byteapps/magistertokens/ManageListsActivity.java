/*
 * Copyright 2017 Bas van den Boom 'Z3r0byte'
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.z3r0byteapps.magistertokens;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import eu.z3r0byteapps.magistertokens.Adapters.ListAdapter;
import eu.z3r0byteapps.magistertokens.Container.List;
import eu.z3r0byteapps.magistertokens.Container.Token;
import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.ListDatabase;
import eu.z3r0byteapps.magistertokens.Util.NavigationDrawer;
import eu.z3r0byteapps.magistertokens.Util.TokenDatabase;

public class ManageListsActivity extends AppCompatActivity {

    private static final String TAG = "ManageListsActivity";

    private final static Integer PICKFILE_REQUEST_CODE = 1000;
    private final static Integer PERMISSION_READ_FILES = 2000;

    Context context = this;
    ConfigUtil configUtil;

    ListAdapter listAdapter;
    ListView listView;
    ListDatabase listDatabase;

    List[] lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lists);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.msg_manage_lists);
        setSupportActionBar(toolbar);

        IconicsDrawable fabIcon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_playlist_add)
                .color(Color.WHITE)
                .sizeDp(18);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(fabIcon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.msg_import_list);
                builder.setMessage(R.string.msg_import_list_clarification);
                builder.setPositiveButton(getString(R.string.btn_import_list), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= 23 &&
                                ContextCompat.checkSelfPermission(ManageListsActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(ManageListsActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_READ_FILES);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            startActivityForResult(intent, PICKFILE_REQUEST_CODE);
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        configUtil = new ConfigUtil(this);
        listDatabase = new ListDatabase(this);

        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, false, 0, "manageLists");
        navigationDrawer.setupNavigationDrawer();

        listView = (ListView) findViewById(R.id.listsListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listDatabase.setPreferred(lists[i]);
                updateLists();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeList(lists[i]);
                return true;
            }
        });
        updateLists();

    }

    private void removeList(final List list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.msg_delete_list_title);
        builder.setMessage(String.format(getString(R.string.msg_delete_list_body), list.getName()));
        builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listDatabase.deleteList(list);
                TokenDatabase tokenDatabase = new TokenDatabase(getApplicationContext());
                tokenDatabase.deleteTokensOfList(list);
                Toast.makeText(ManageListsActivity.this, getString(R.string.msg_list_deleted), Toast.LENGTH_SHORT).show();
                updateLists();
            }
        });
        builder.setNegativeButton(R.string.msg_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.msg_help_title);
        builder.setMessage(Html.fromHtml(getString(R.string.msg_help_body)));
        builder.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateLists() {
        IconicsDrawable noListIcon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_storage)
                .color(getResources().getColor(R.color.divider))
                .sizeDp(75);

        ArrayList<List> listsArrayList = listDatabase.getLists();
        if (listsArrayList == null || listsArrayList.size() == 0) {
            ImageView noLists = (ImageView) findViewById(R.id.no_lists_image);
            noLists.setImageDrawable(noListIcon);
        } else {
            RelativeLayout noLists = (RelativeLayout) findViewById(R.id.no_lists_layout);
            noLists.setVisibility(View.GONE);
            lists = listsArrayList.toArray(new List[listsArrayList.size()]);
            listAdapter = new ListAdapter(this, lists);
            listView.setAdapter(listAdapter);
        }
    }

    private void loadFile(String uri) {
        String path = uri.replace("/document/primary:", "");
        path = path.replace("/document/home:", "Documents/");
        Log.d(TAG, "loadFile: Path: " + path);
        File sdcard = Environment.getExternalStorageDirectory();

        File list = new File(sdcard, path);

        String occuredErrors = "";
        ArrayList<Token> tokens = new ArrayList<>();
        if (list.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(list));
                String line;

                while ((line = br.readLine()) != null) {
                    try {
                        int endIndex;
                        if (line.contains(";")) {
                            endIndex = line.indexOf(";");
                        } else if (line.contains(",")) {
                            endIndex = line.indexOf(",");
                        } else if (line.contains(":")) {
                            endIndex = line.indexOf(":");
                        } else {
                            throw new UnsupportedEncodingException("Niet het juiste scheidingsteken!");
                        }
                        String idStr = line.substring(0, endIndex);
                        String tokenStr = line.substring(endIndex + 1, line.length());

                        Token token = new Token(Integer.parseInt(idStr), tokenStr);
                        tokens.add(token);
                    } catch (Exception e) {
                        e.printStackTrace();
                        occuredErrors = occuredErrors + line + "\n";
                    }
                }
                br.close();
            } catch (IOException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.err_oops);
                builder.setMessage(R.string.err_something_went_wrong + e.getMessage().toString());
                builder.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                e.printStackTrace();
                return;
            }
            if (occuredErrors != "") {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.err_oops);
                builder.setMessage(getString(R.string.err_unable_to_read_lines) + occuredErrors);
                builder.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Token[] tokensArray = new Token[tokens.size()];
                tokensArray = tokens.toArray(tokensArray);
                final Token[] tokensArrayFinal = tokensArray;

                AlertDialog.Builder insertName = new AlertDialog.Builder(context);
                insertName.setTitle(R.string.msg_insert_name);
                insertName.setMessage(R.string.msg_insert_name_for_list);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                insertName.setView(input);
                insertName.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveList(tokensArrayFinal, input.getText().toString());
                    }
                });
                insertName.setNegativeButton(R.string.msg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                insertName.show();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.err_oops);
            builder.setMessage(R.string.err_retrieving_file);
            builder.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void saveList(Token[] tokens, String listName) {
        try {
            List list;

            TokenDatabase tokenDatabase = new TokenDatabase(this);
            ListDatabase listDatabase = new ListDatabase(this);

            if (listDatabase.getAmountOfLists() == 0) {
                list = new List(listName, true, tokens.length);
            } else {
                list = new List(listName, false, tokens.length);
                if (listDatabase.listExists(list)) {
                    Toast.makeText(context, R.string.err_list_exsists, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            tokenDatabase.addItems(tokens, listName);
            listDatabase.addList(list);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.msg_success);
            builder.setMessage(R.string.msg_successfully_imported_list);
            builder.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(context, BootActivity.class));
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.err_oops);
            builder.setMessage(R.string.err_something_went_wrong + e.getMessage());
            builder.setPositiveButton(R.string.msg_okay, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKFILE_REQUEST_CODE && data != null) {
            loadFile(data.getData().getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICKFILE_REQUEST_CODE);
                } else {
                    Toast.makeText(context, R.string.msg_allow_read_storage_please, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            showHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
