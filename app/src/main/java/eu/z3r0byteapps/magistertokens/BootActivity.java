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
import android.support.v7.app.AppCompatActivity;

import eu.z3r0byteapps.magistertokens.Util.ConfigUtil;

public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        ConfigUtil configUtil = new ConfigUtil(this);

        if (configUtil.getInteger("amountOfLists", 0) < 1) {
            startActivity(new Intent(this, ManageListsActivity.class));
        } else {
            //start favorite list activity
        }

    }
}
