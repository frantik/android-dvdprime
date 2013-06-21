/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.dvdprime.android.app.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;

public class AccountSettingActivity extends BaseActivity {

	// UI ACTIONs
	static final int LOGIN_FAIL = 0;
	static final int WRONG_INFO = 1;
	static final int WRONG_ID   = 2;
	static final int WRONG_PW   = 3;
	static final int NETWORK_ERROR = 4;
	static final int LOGIN_SUCCESS = 5;

    // Activity result type
	final static int RESULT_ACCOUNT_SETTING = 1;

	private Context mAppContext = null;
	private PrefUtil mPref = null;

	private TextView mTextViewId;
	private EditText mEditTextLogin;
	private EditText mEditTextPassword;
	private CheckBox mCheckboxShowPassword;
	private Button loginButton;

	private String mUserName = null;
	private String mPassword = null;
	
	public AccountSettingActivity() {
		super(R.layout.account_setting_layout);
	}
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		mAppContext = this;
		mPref = PrefUtil.getInstance();

		this.setTitle(R.string.account_set);		
		
    	mTextViewId = ((TextView)findViewById(R.id.text_username));
		mEditTextLogin = ((EditText)findViewById(R.id.new_account_username));
		mEditTextPassword = (EditText)findViewById(R.id.new_account_password);
		mCheckboxShowPassword = ((CheckBox)findViewById(R.id.new_show_password));
		loginButton = (Button)findViewById(R.id.new_account_login);

		mTextViewId.setText(getString(R.string.id));
		mEditTextLogin.setHint(getString(R.string.id));

		mCheckboxShowPassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				int currentPosition = mEditTextPassword.getSelectionEnd();

				if (mCheckboxShowPassword.isChecked()) {
					mEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					mEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}

				mEditTextPassword.setSelection(currentPosition);
			}
		});

		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				mUserName = mEditTextLogin.getText().toString();
				mPassword = mEditTextPassword.getText().toString();

				if (mUserName.length() <= 0 )
					showDialog(WRONG_ID);
				else if(mPassword.length() <= 0)
					showDialog(WRONG_PW);
				else
					execLogin();
			}
		});
	}

    @Override
    public void onResume() {
        super.onResume();
    }
    
	@Override
	public final void handleEvent(final DataEvent event) {
		if (event.getData() != null) {
			if (event.getType() == DataEvent.LOGIN) {
    			switch ((Integer)event.getData()) {
	    			case Const.LOGIN_FAILED:
						UIThreadRunnable wrongEmailOrPwd = new UIThreadRunnable(mAppContext, AccountSettingActivity.WRONG_INFO);
						((AccountSettingActivity)mAppContext).runOnUiThread(wrongEmailOrPwd);
	    				break;
	
	    			case Const.SERVICE_UNAVAILABLE:
						UIThreadRunnable loginFailByAnother = new UIThreadRunnable(mAppContext, AccountSettingActivity.LOGIN_FAIL);
						((AccountSettingActivity)mAppContext).runOnUiThread(loginFailByAnother);
	    				break;
	
	    			case Const.OK:
	    				returnLoginSuccessResult();
	    				break;
    			}
			}
		}
		hideLoadingIndicator(true);
		super.handleEvent(event);
	}
	
    @Override
	public void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {        
		case LOGIN_FAIL:
			AlertDialog alert = (AlertDialog) dialog;
			alert.setMessage(getString(R.string.login_failed_message));                
			break;
		case WRONG_ID:
			AlertDialog alert1 = (AlertDialog) dialog;
			alert1.setMessage(getString(R.string.wrong_id));                
			break;
		case WRONG_PW:
			AlertDialog alert2 = (AlertDialog) dialog;
			alert2.setMessage(getString(R.string.wrong_password));                
			break;
		case WRONG_INFO:
			AlertDialog alert3 = (AlertDialog) dialog;
			alert3.setMessage(getString(R.string.wrong_id_or_pwd));                
			break;
		case NETWORK_ERROR:
			AlertDialog alert5 = (AlertDialog) dialog;
			alert5.setMessage(getString(R.string.network_unavailable));                
			break;	
		}
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
		case LOGIN_FAIL:
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.login_failed)
			.setMessage(getString(R.string.login_failed_message))
			.setPositiveButton(R.string.okay_action,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(LOGIN_FAIL);
				}
			})
			.create();

		case WRONG_ID:
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.login_failed)
			.setMessage(getString(R.string.wrong_id))
			.setPositiveButton(R.string.okay_action,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(WRONG_ID);
				}
			})
			.create();

		case WRONG_PW:
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.login_failed)
			.setMessage(getString(R.string.wrong_password))
			.setPositiveButton(R.string.okay_action,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(WRONG_PW);
				}
			})
			.create();

		case WRONG_INFO:
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.login_failed)
			.setMessage(getString(R.string.wrong_id_or_pwd))
			.setPositiveButton(R.string.okay_action,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(WRONG_INFO);
				}
			})
			.create();

		case NETWORK_ERROR:
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.login_failed)
			.setMessage(getString(R.string.network_unavailable))
			.setPositiveButton(R.string.okay_action,
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(NETWORK_ERROR);
				}
			})
			.create();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
    	mAppContext = null;
		super.onDestroy();		
	}

	public void returnLoginSuccessResult(){
		mPref.setString(PreferenceKeys.ACCOUNT_ID, mUserName);
		mPref.setString(PreferenceKeys.ACCOUNT_PW, mPassword);
		mPref.setLong(PreferenceKeys.ACCOUNT_TIME, System.currentTimeMillis());

		Toast mToast = Toast.makeText(mAppContext, null, Toast.LENGTH_SHORT);
		mToast.setText(R.string.login_success);
		mToast.show();
		
        finish();
	}

	public Context getAuthenticatorContext()  {
		return mAppContext;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == 0)) {    
			finish();
		}
	}
	
	/**
	 * 로그인 실행
	 */
	private void execLogin() {
		if(DpUtil.isNetWorkConnected(getApplicationContext())) 
        {
			AccountManager am = AccountManager.getInstance();
			am.addConsumer(this, DataEvent.LOGIN);
			am.login(mUserName, mPassword);
			showLoadingIndicator(R.string.bbs_progressbar_login);
        } else {
			UIThreadRunnable networkFail = new UIThreadRunnable(mAppContext, AccountSettingActivity.NETWORK_ERROR);
			((AccountSettingActivity)mAppContext).runOnUiThread(networkFail);
        }
	}
}

class UIThreadRunnable implements Runnable {

	Context mAppContext = null;
	int mActionType = -1;

	UIThreadRunnable(Context ctxt, int actionType) {
		mAppContext = ctxt;
		mActionType = actionType;
	}

	public void run() {
		switch (mActionType) {

		case AccountSettingActivity.LOGIN_FAIL:
			((AccountSettingActivity)mAppContext).showDialog(AccountSettingActivity.LOGIN_FAIL);
			break;
		case AccountSettingActivity.WRONG_INFO:
			((AccountSettingActivity)mAppContext).showDialog(AccountSettingActivity.WRONG_INFO);
			break;
		case AccountSettingActivity.NETWORK_ERROR:
			((AccountSettingActivity)mAppContext).showDialog(AccountSettingActivity.NETWORK_ERROR);
			break;
			
		default:
			break;
		}
	}
}
