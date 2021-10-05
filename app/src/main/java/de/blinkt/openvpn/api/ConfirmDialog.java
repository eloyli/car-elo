/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.blinkt.openvpn.api;

import android.app.*;
import android.app.AlertDialog.*;
import android.content.*;
import android.content.DialogInterface.*;
import android.content.pm.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.masfha.id.*;

import android.content.DialogInterface.OnShowListener;

public class ConfirmDialog extends Activity implements CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener {
    private static final String TAG = "OpenVPNVpnConfirm";
    private String mPackage;
    private Button mButton;
    private AlertDialog mAlert;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mPackage = getCallingPackage();
            if (mPackage == null) {
                finish();
                return;
            }
            PackageManager pm = getPackageManager();
            ApplicationInfo app = pm.getApplicationInfo(mPackage, 0);
            View view = View.inflate(this, R.layout.api_confirm, null);
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(app.loadIcon(pm));
            ((TextView) view.findViewById(R.id.prompt)).setText(getString(R.string.prompt, app.loadLabel(pm), getString(R.string.app_name)));
            ((CompoundButton) view.findViewById(R.id.check)).setOnCheckedChangeListener(this);
            Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            builder.setIconAttribute(android.R.attr.alertDialogIcon);
            builder.setTitle(android.R.string.dialog_alert_title);
            builder.setPositiveButton(android.R.string.ok, this);
            builder.setNegativeButton(android.R.string.cancel, this);
            mAlert = builder.create();
            mAlert.setCanceledOnTouchOutside(false);
            mAlert.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mButton = mAlert.getButton(DialogInterface.BUTTON_POSITIVE);
                    mButton.setEnabled(false);
                }
            });
            //setCloseOnTouchOutside(false);
            mAlert.show();
        } catch (Exception e) {
            Log.e(TAG, "onResume", e);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        mButton.setEnabled(checked);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ExternalAppDatabase extapps = new ExternalAppDatabase(this);
            extapps.addApp(mPackage);
            setResult(RESULT_OK);
            finish();
        }
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
