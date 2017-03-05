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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.digitus.Digitus;
import com.afollestad.digitus.FingerprintDialog;
import com.afollestad.materialdialogs.MaterialDialog;

import java.security.MessageDigest;
import java.util.Date;

import eu.z3r0byteapps.magistertokens.Container.License;
import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;
import eu.z3r0byteapps.magistertokens.Util.DateUtils;
import eu.z3r0byteapps.magistertokens.Util.LicenseUtil;
import eu.z3r0byteapps.magistertokens.Util.ListDatabase;

public class BootActivity extends AppCompatActivity implements FingerprintDialog.Callback {
    private static final String TAG = "BootActivity";

    ConfigUtil configUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        configUtil = new ConfigUtil(this);

        if (configUtil.getBoolean("fingerprintSet", false)) {
            FingerprintDialog.show(this, getString(R.string.app_name), 69, false);
        } else if (configUtil.getBoolean("passwordSet", false)) {
            passwordDialog();
        } else {
            initiateApp();
        }

    }

    private void passwordDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.msg_enter_password)
                .content(R.string.msg_enter_password_to_continue)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(getString(R.string.hint_password), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (createHash(input.toString(), "SHA-256").equals(configUtil.getString("password", null))) {
                            initiateApp();
                        } else {
                            Toast.makeText(BootActivity.this, R.string.err_password_incorrect, Toast.LENGTH_SHORT).show();
                            passwordDialog();
                        }
                    }
                }).show();
    }

    private void initiateApp() {
        ListDatabase listDatabase = new ListDatabase(this);
        if (configUtil.getBoolean("isTrial", true)) {
            if (DateUtils.isAfter(DateUtils.parseDate(configUtil.getString("endDate", "2000-10-10 12:00")
                    , "yyyy-MM-dd HH:mm"), new Date())) {
                License license = LicenseUtil.getLicense(this);
                configUtil.setString("endDate", license.endDate);
                configUtil.setBoolean("isValid", license.valid);
                configUtil.setBoolean("isTrial", license.isTrial);
            }
        }

        if (!configUtil.getBoolean("isValid", false)) {
            startActivity(new Intent(this, SettingsActivity.class));
            Toast.makeText(this, getString(R.string.err_license_invalid), Toast.LENGTH_SHORT).show();
            finish();
        } else {


            if (configUtil.getBoolean("first_start", true)) {
                configUtil.setBoolean("first_start", false);
                startActivity(new Intent(this, ManageListsActivity.class));
                finish();
            }

            if (listDatabase.getAmountOfLists() < 1 || listDatabase.getPreferredList() == null) {
                startActivity(new Intent(this, ManageListsActivity.class));
                finish();
            } else {
                String listName = listDatabase.getPreferredList();
                Intent intent = new Intent(this, TokenActivity.class);
                intent.putExtra("listName", listName);
                startActivity(intent);
                finish();
            }
        }
    }

    public static String createHash(String data, String function) {
        try {
            MessageDigest digest = MessageDigest.getInstance(function);
            byte[] hash = digest.digest((data).getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Notify Digitus of the result
        Digitus.get().handleResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFingerprintDialogAuthenticated() {
        initiateApp();
    }

    @Override
    public void onFingerprintDialogVerifyPassword(FingerprintDialog dialog, final String password) {
        dialog.notifyPasswordValidation(createHash(password, "SHA-256").equals(configUtil.getString("password", null)));
    }

    @Override
    public void onFingerprintDialogStageUpdated(FingerprintDialog dialog, FingerprintDialog.Stage stage) {
        Log.d("Digitus", "Dialog stage: " + stage.name());
    }

    @Override
    public void onFingerprintDialogCancelled() {
        finish();
    }
}
