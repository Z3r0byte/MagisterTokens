package eu.z3r0byteapps.magistertokens;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.digitus.Digitus;
import com.afollestad.digitus.DigitusCallback;
import com.afollestad.digitus.DigitusErrorType;
import com.afollestad.materialdialogs.MaterialDialog;

import java.security.MessageDigest;

import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityFragment extends Fragment implements DigitusCallback {
    private static final String TAG = "SecurityFragment";

    private static final Integer DISABLE_FINGERPRINT = 1;
    private static final Integer CHANGE_PASSWORD = 2;
    private static final Integer DISABLE_PASSWORD = 3;
    private static final Integer ENABLE_PASSWORD = 4;

    View view;
    Digitus digitus;
    ConfigUtil configUtil;

    Switch fingerprintSwitch;
    Switch passwordSwitch;
    TextView changePassword;
    TextView securityInfo;

    Integer requestCode;
    String firstInput;

    public SecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_security, container, false);
        Digitus.init(getActivity(), getString(R.string.app_name), 69, this);
        configUtil = new ConfigUtil(getActivity());

        fingerprintSwitch = (Switch) view.findViewById(R.id.enableFingerprint);
        passwordSwitch = (Switch) view.findViewById(R.id.enablePassword);
        changePassword = (TextView) view.findViewById(R.id.changePassword);
        securityInfo = (TextView) view.findViewById(R.id.securityInfo);


        if (!digitus.isFingerprintAuthAvailable()) {
            Log.e(TAG, "onCreateView: Fingerprint Sensor is not available on this device");
            fingerprintSwitch.setEnabled(false);
        }

        loadSettings();

        fingerprintSwitch.setTag("TAG");
        fingerprintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (fingerprintSwitch.getTag() != null) {
                    return;
                }
                fingerprintSwitch.setTag("TAG");

                if (b) {
                    if (!configUtil.getBoolean("passwordSet", false)) {
                        fingerprintSwitch.setChecked(false);
                        Snackbar.make(view, R.string.err_set_pwd_first, Snackbar.LENGTH_SHORT).show();
                    } else {
                        fingerprintSwitch.setChecked(false);
                        if (!digitus.isFingerprintRegistered()) {
                            Snackbar.make(view, getString(R.string.err_register_fingerprints_first), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.msg_fix), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            digitus.openSecuritySettings();
                                        }
                                    })
                                    .show();
                        } else {
                            Snackbar.make(view, R.string.msg_touch_fingerprintsensor, Snackbar.LENGTH_LONG)
                                    .show();
                            digitus.startListening();
                        }
                    }
                } else {
                    fingerprintSwitch.setChecked(true);
                    requestCode = DISABLE_FINGERPRINT;
                    authPassword();
                }
            }
        });

        fingerprintSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fingerprintSwitch.setTag(null);
                return false;
            }
        });

        passwordSwitch.setTag("TAG");
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (passwordSwitch.getTag() != null) {
                    return;
                }
                passwordSwitch.setTag("TAG");

                if (b) {
                    passwordSwitch.setChecked(false);
                    setUpPassword();
                } else {
                    passwordSwitch.setChecked(true);
                    requestCode = DISABLE_PASSWORD;
                    authPassword();
                }
            }
        });

        passwordSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                passwordSwitch.setTag(null);
                return false;
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCode = CHANGE_PASSWORD;
                authPassword();
            }
        });

        securityInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.msg_security_info_head)
                        .content(Html.fromHtml(getString(R.string.msg_security_info_body)))
                        .positiveText(R.string.msg_okay)
                        .show();
            }
        });
        return view;
    }

    private void setUpPassword() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.msg_enter_a_password)
                .content(R.string.msg_remember_your_password)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(getString(R.string.hint_password), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        firstInput = input.toString();
                        confirmPassword();
                    }
                }).show();
    }

    private void confirmPassword() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.msg_reenter_your_password)
                .content(R.string.msg_enter_password_again_to_confirm)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(getString(R.string.hint_password), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().equals(firstInput)) {
                            configUtil.setBoolean("passwordSet", true);
                            configUtil.setString("password", createHash(input.toString(), "SHA-256"));
                            Snackbar.make(view, getString(R.string.msg_password_set), Snackbar.LENGTH_SHORT).show();
                            passwordSwitch.setChecked(true);
                            changePassword.setEnabled(true);
                        } else {
                            Snackbar.make(view, R.string.err_passwords_do_not_match, Snackbar.LENGTH_SHORT).show();
                        }
                        firstInput = null;
                    }
                }).show();
    }


    private void authPassword() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.msg_enter_password)
                .content(R.string.msg_enter_password_to_continue)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input(getString(R.string.hint_password), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (createHash(input.toString(), "SHA-256").equals(configUtil.getString("password", null))) {
                            authSuccess();
                        } else {
                            Snackbar.make(view, getString(R.string.err_password_incorrect), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    private void authSuccess() {
        if (requestCode == DISABLE_FINGERPRINT) {
            configUtil.setBoolean("fingerprintSet", false);
            fingerprintSwitch.setChecked(false);
        } else if (requestCode == DISABLE_PASSWORD) {
            configUtil.setBoolean("passwordSet", false);
            configUtil.setBoolean("fingerprintSet", false);
            configUtil.setString("password", null);
            passwordSwitch.setChecked(false);
            fingerprintSwitch.setChecked(false);
        } else if (requestCode == CHANGE_PASSWORD) {
            setUpPassword();
        }
    }

    private void loadSettings() {
        fingerprintSwitch.setChecked(configUtil.getBoolean("fingerprintSet", false));
        passwordSwitch.setChecked(configUtil.getBoolean("passwordSet", false));
        changePassword.setEnabled(configUtil.getBoolean("passwordSet", false));
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
    public void onDigitusReady(Digitus digitus) {
        Log.d(TAG, "onDigitusReady: ready!");
        this.digitus = digitus;
    }

    @Override
    public void onDigitusAuthenticated(Digitus digitus) {
        Snackbar.make(view, getString(R.string.msg_fingerprint_recognised), Snackbar.LENGTH_SHORT).show();
        configUtil.setBoolean("fingerprintSet", true);
        fingerprintSwitch.setChecked(true);
    }

    @Override
    public void onDigitusError(Digitus digitus, DigitusErrorType type, Exception e) {
        this.digitus = digitus;
        switch (type) {
            case FINGERPRINT_NOT_RECOGNIZED:
                Snackbar.make(view, R.string.err_fingerprint_not_recognised, Snackbar.LENGTH_LONG)
                        .show();
                digitus.startListening();
                break;
            case FINGERPRINTS_UNSUPPORTED:
                // Fingerprints are not supported by the device (e.g. no sensor, or no API support).
                // You should fallback to password authentication.
                break;
            case HELP_ERROR:
                Snackbar.make(view, String.format(getString(R.string.err_error_format), e.getMessage()),
                        Snackbar.LENGTH_LONG)
                        .show();
                digitus.startListening();
                break;
            case PERMISSION_DENIED:
                // The USE_FINGERPRINT permission was denied by the user or device.
                // You should fallback to password authentication.
                break;
            case REGISTRATION_NEEDED:
                // There are no fingerprints registered on the device.
                // You can open the Security Settings system screen using the code below...
                // ...but probably with a button click instead of doing it automatically.
                break;
            case UNRECOVERABLE_ERROR:
                // An recoverable error occurred, no further callbacks are sent until you start listening again.
                break;
        }
    }

    @Override
    public void onDigitusListening(boolean newFingerprint) {
        Log.d(TAG, "onDigitusListening: Listening");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Digitus.get().handleResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Digitus.deinit();
    }
}
