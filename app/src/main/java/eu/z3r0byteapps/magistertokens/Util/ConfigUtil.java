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

package eu.z3r0byteapps.magistertokens.Util;

import android.content.Context;
import android.content.SharedPreferences;


public class ConfigUtil {

    private static final String PREFS_NAME = "data";

    private Context applicationContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /*
    constructors
     */

    public ConfigUtil(Context applicationContext) {
        this.applicationContext = applicationContext;
        initiatePreferences(applicationContext);
    }

    private void initiatePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    /*
    setters
     */

    public void setString(String key, String value) {
        if (sharedPreferences != null && editor != null) {
            editor.putString(key, value);
            editor.apply();
        } else {
            initiatePreferences(applicationContext);
            setString(key, value);
        }
    }

    public void setBoolean(String key, Boolean value) {
        if (sharedPreferences != null && editor != null) {
            editor.putBoolean(key, value);
            editor.apply();
        } else {
            initiatePreferences(applicationContext);
            setBoolean(key, value);
        }
    }

    public void setInteger(String key, Integer value) {
        if (sharedPreferences != null && editor != null) {
            editor.putInt(key, value);
            editor.apply();
        } else {
            initiatePreferences(applicationContext);
            setInteger(key, value);
        }
    }

    /*
    getters
    */

    public String getString(String key, String defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defaultValue);
        } else {
            initiatePreferences(applicationContext);
            return getString(key, defaultValue);
        }
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        } else {
            initiatePreferences(applicationContext);
            return getBoolean(key, defaultValue);
        }
    }

    public Integer getInteger(String key, Integer defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, defaultValue);
        } else {
            initiatePreferences(applicationContext);
            return getInteger(key, defaultValue);
        }
    }
}
