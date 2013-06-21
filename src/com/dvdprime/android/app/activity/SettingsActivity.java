/*
 * Copyright (C) 2007 The Android Open Source Project
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

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.dialog.ListPreferenceMultiSelect;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * DP preference settings.
 */
public class SettingsActivity extends PreferenceActivity {

	private Activity mActivity;
	private Handler mHandler;
	private PrefUtil mPref;

	// settings state
    private final int AUTO_LOGIN_CHECKED = 1;
    private final int AUTO_LOGIN_UNCHECKED = 2;
    private final int ACCOUNT_RESET_OK = 3;

    // dialog
	private final int ACCOUNT_RESET_DIALOG = 1;
	private final int TRY_LOGGIN_DIALOG = 2;
    
	// Account
    private PreferenceScreen mAccountPreferences;
	private CheckBoxPreference mAutoAlertMemoCheckBox;
    private EditText mAccountId;
    private EditText mAccountPw;
    private boolean mIsDlgOn = false;
    String newAccountPwd = null;
    AccountStorage mAccountStorage = new AccountStorage();
    
    // 필터
    FilterStorage mFilterStorage = new FilterStorage();
    // 고급
    AdvanceStorage mAdvanceStorage = new AdvanceStorage();
    
    // About
    private PreferenceScreen mAboutPreferences;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mActivity = this;
        mHandler = new Handler();
        mPref = PrefUtil.getInstance();

    	addPreferencesFromResource(R.xml.general_settings);

        createPreferenceHierarchy();
    }

    @SuppressWarnings("deprecation")
	private PreferenceScreen createPreferenceHierarchy() {
		// Root
		PreferenceScreen root = this.getPreferenceScreen();

        // Account Setting
        PreferenceCategory accountCat = new PreferenceCategory(this);
        accountCat.setTitle(R.string.header_account_settings);
	        mAccountPreferences = getPreferenceManager().createPreferenceScreen(this);
	        mAccountPreferences.setTitle(R.string.account_set);
	        mAccountPreferences.setSummary(R.string.account_set_off_summary);
	        // Intent to launch About settings
	        Intent intent = new Intent();
			intent.setClassName("com.dvdprime.android.app", AccountSettingActivity.class.getName());
			mAccountPreferences.setIntent(intent);
			root.addPreference(accountCat);
			accountCat.addPreference(mAccountPreferences);
        root.addPreference(accountCat);
        mAccountStorage.createPreferences(accountCat);
        
        // Filter Setting
        PreferenceCategory filterCat = new PreferenceCategory(this);
        filterCat.setTitle(R.string.header_filter_settings);
		root.addPreference(filterCat);
		mFilterStorage.createPreferences(filterCat);
        
        // Advance Setting
        PreferenceCategory advanceCat = new PreferenceCategory(this);
        advanceCat.setTitle(R.string.header_advance_settings);
		root.addPreference(advanceCat);
		mAdvanceStorage.createPreferences(advanceCat);
        
		// About DPApp
        mAboutPreferences = getPreferenceManager().createPreferenceScreen(this);
        mAboutPreferences.setTitle(String.format(getString(R.string.about_setting_title), SystemUtil.getVersionName(this)));
		// Intent to launch About settings
        intent = new Intent();
		intent.setClassName("com.dvdprime.android.app", AboutActivity.class.getName());
		mAboutPreferences.setIntent(intent);

		PreferenceCategory aboutCat = new PreferenceCategory(this);
		aboutCat.setTitle(R.string.information_category);
		root.addPreference(aboutCat);
		aboutCat.addPreference(mAboutPreferences);

		return root;
    }

    @Override
    protected void onResume() {
		super.onResume();
		
        mHandler.postDelayed(new Runnable() {
        	public void run() {
				if (StringUtil.isNotEmpty(mPref.getString(PreferenceKeys.ACCOUNT_ID, null))) {
					mAccountPreferences.setSummary(R.string.account_set_on_summary);
					mAccountPreferences.setEnabled(false);
					mAccountStorage.mResetButton.setEnabled(true);
//					mAccountStorage.mAutoLoginCheckBox.setChecked(true);
					mAccountStorage.mAutoLoginCheckBox.setEnabled(true);
					
				} else {
					// login setting values initialization
					mPref.removePref(PreferenceKeys.ACCOUNT_ID);
					mPref.removePref(PreferenceKeys.ACCOUNT_PW);
					mPref.removePref(PreferenceKeys.ACCOUNT_TIME);
					
					mAccountPreferences.setSummary(R.string.account_set_off_summary);
					mAccountPreferences.setEnabled(true);
					mAccountStorage.mResetButton.setEnabled(false);
					mAccountStorage.mAutoLoginCheckBox.setEnabled(false);
				}
        	}
       	}, 100);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();		
	}

    @Override
    protected Dialog onCreateDialog(int id) {
    	if (id == ACCOUNT_RESET_DIALOG)
    		return mAccountStorage.createDialog(id);
    	else if (id == TRY_LOGGIN_DIALOG)
    		return mAccountStorage.createDialog(id);
    	
		return null;
    }
    
    private class AccountStorage implements DialogInterface.OnClickListener,
								    DialogInterface.OnDismissListener, Preference.OnPreferenceChangeListener,
								    Preference.OnPreferenceClickListener {
		
        private Preference mResetButton;
        private CheckBoxPreference mAutoLoginCheckBox;

		Dialog createDialog(int id) {
		    switch (id) {
		        case ACCOUNT_RESET_DIALOG:
		            return new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.account_reset_hint)
                    .setNeutralButton(getString(android.R.string.ok), this)
                    .setNegativeButton(getString(android.R.string.cancel), this)
                    .create();
		    }
		    return null;
		}

		private void createPreferences(PreferenceCategory category) {
		    mResetButton = new Preference(SettingsActivity.this);
		    mResetButton.setTitle(R.string.account_reset);
		    mResetButton.setSummary(R.string.account_reset_summary);
		    mResetButton.setOnPreferenceClickListener(this);
		    category.addPreference(mResetButton);
		
		    mAutoLoginCheckBox = new CheckBoxPreference(SettingsActivity.this);
		    mAutoLoginCheckBox.setKey(PreferenceKeys.AUTO_LOGIN_ENABLED);
		    mAutoLoginCheckBox.setTitle(R.string.account_autologin);
		    mAutoLoginCheckBox.setSummary(R.string.account_autologin_summary);
		    mAutoLoginCheckBox.setOnPreferenceChangeListener(this);
		    category.addPreference(mAutoLoginCheckBox);
		}

        private void updatePreferences(int state) {
        	mResetButton.setEnabled(!(state == ACCOUNT_RESET_OK));
        	mAutoLoginCheckBox.setChecked(state == AUTO_LOGIN_CHECKED);
        }

        public void onClick(DialogInterface dialog, int button) {
        	// account delete
            if (button == DialogInterface.BUTTON_NEUTRAL) {
				// login setting values initialization
				mPref.removePref(PreferenceKeys.ACCOUNT_ID);
				mPref.removePref(PreferenceKeys.ACCOUNT_PW);
				mPref.removePref(PreferenceKeys.ACCOUNT_TIME);

				DBAdapter dbAdapter = DBAdapter.getInstance();
				ContentValues cv = new ContentValues();
				cv.put(DpDB.Account.ACCOUNT_NAME, "");
				cv.put(DpDB.Account.ACCOUNT_AVARTAR, "");
				dbAdapter.updateAccount(cv);
				dbAdapter.close();

				mAccountPreferences.setSummary(R.string.account_set_off_summary);
				mAccountPreferences.setEnabled(true);
				mAutoLoginCheckBox.setChecked(false);
				mAutoLoginCheckBox.setEnabled(false);
				mAutoAlertMemoCheckBox.setChecked(false);
				updatePreferences(ACCOUNT_RESET_OK);
            }
		}

        public boolean onPreferenceChange(Preference preference, Object value) {
            if (preference == mAutoLoginCheckBox) {
                if ((Boolean) value) {
                	updatePreferences(AUTO_LOGIN_CHECKED);
                } else {
                	updatePreferences(AUTO_LOGIN_UNCHECKED);
                }
            }
            return false;
		}

		@SuppressWarnings("deprecation")
		public boolean onPreferenceClick(Preference preference) {
			if (preference == mResetButton) {
	        	showDialog(ACCOUNT_RESET_DIALOG);
	        } else {
	            return false;
	        }
	        return true;
		}
		
		public void onDismiss(DialogInterface arg0) {
			// TODO Auto-generated method stub
			
		}
	}
    
    private class FilterStorage implements Preference.OnPreferenceClickListener {
    	
    	private PreferenceScreen filterScreen;
    	private ListPreferenceMultiSelect filterUserList;
    	private ListPreferenceMultiSelect filterItemList;
    	
		@SuppressWarnings("deprecation")
		private void createPreferences(PreferenceCategory category) {
    		
			String[] entries = StringUtil.split(mPref.getString(PreferenceKeys.FILTERING_NICK_LIST, null), Const.DEFAULT_SEPARATOR);
			String[] values = StringUtil.split(mPref.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR);

    		if (entries != null && entries.length > 0) {
        		filterUserList = new ListPreferenceMultiSelect(SettingsActivity.this, 1);
	    		filterUserList.setEntries(entries);
	    		filterUserList.setEntryValues(values);
	    		filterUserList.setDialogTitle(R.string.dialog_filter_list_title);
	    		filterUserList.setKey(PreferenceKeys.FILTERING_ID_LIST);
	    		filterUserList.setTitle(R.string.filter_user_list_title);
	    		filterUserList.setSummary(R.string.filter_user_list_summary);
	    		category.addPreference(filterUserList);
    		} else {
    			filterScreen = getPreferenceManager().createPreferenceScreen(SettingsActivity.this);
    			filterScreen.setKey(PreferenceKeys.FILTERING_ID_LIST);
    			filterScreen.setTitle(R.string.filter_user_list_title);
    			filterScreen.setSummary(R.string.filter_user_list_nothing_summary);
    			filterScreen.setEnabled(false);
	    		category.addPreference(filterScreen);
    		}

    		filterItemList = new ListPreferenceMultiSelect(SettingsActivity.this);
    		filterItemList.setEntries(R.array.entries_filter_item);
    		filterItemList.setEntryValues(R.array.entryvalues_filter_item);
    		filterItemList.setDialogTitle(R.string.dialog_filter_item_title);
    		filterItemList.setKey(PreferenceKeys.FILTERING_ITEM_LIST);
    		filterItemList.setTitle(R.string.filter_item_list_title);
    		filterItemList.setSummary(R.string.filter_item_list_summary);
    		category.addPreference(filterItemList);
    	}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			if (preference == filterUserList) {
	    		filterUserList.setEntries(StringUtil.split(mPref.getString(PreferenceKeys.FILTERING_NICK_LIST, null), Const.DEFAULT_SEPARATOR));
	    		filterUserList.setEntryValues(StringUtil.split(mPref.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR));
			}

			return false;
		}
    	
    }

    private class AdvanceStorage implements Preference.OnPreferenceChangeListener {

    	private CheckBoxPreference mDirectBbsCheckBox;
    	private CheckBoxPreference mSaveCommentCheckBox;
    	private CheckBoxPreference mListWidgetCheckBox;
    	private CheckBoxPreference mContentTextOnlyCheckBox;
    	private CheckBoxPreference mWebViewPluginCheckBox;
    	private CheckBoxPreference mCloseDialogCheckBox;

		private void createPreferences(PreferenceCategory category) {
			mDirectBbsCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mDirectBbsCheckBox.setKey(PreferenceKeys.DIRECT_BBS_ENABLED);
			mDirectBbsCheckBox.setTitle(R.string.direct_bbs);
			mDirectBbsCheckBox.setSummary(R.string.direct_bbs_summary);
			mDirectBbsCheckBox.setDefaultValue(false);
			category.addPreference(mDirectBbsCheckBox);

			mAutoAlertMemoCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mAutoAlertMemoCheckBox.setKey(PreferenceKeys.ALERT_MEMO_ENABLED);
			mAutoAlertMemoCheckBox.setTitle(R.string.alert_memo);
			mAutoAlertMemoCheckBox.setSummary(R.string.alert_memo_summary);
			mAutoAlertMemoCheckBox.setDefaultValue(false);
			mAutoAlertMemoCheckBox.setOnPreferenceChangeListener(this);
			category.addPreference(mAutoAlertMemoCheckBox);

			mSaveCommentCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mSaveCommentCheckBox.setKey(PreferenceKeys.SAVE_COMMENT_ENABLED);
			mSaveCommentCheckBox.setTitle(R.string.save_comment);
			mSaveCommentCheckBox.setSummary(R.string.save_comment_summary);
			mSaveCommentCheckBox.setDefaultValue(true);
			category.addPreference(mSaveCommentCheckBox);

			mListWidgetCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mListWidgetCheckBox.setKey(PreferenceKeys.WIDGET_NAVI_ENABLED);
			mListWidgetCheckBox.setTitle(R.string.navi_widget);
			mListWidgetCheckBox.setSummary(R.string.navi_widget_summary);
			mListWidgetCheckBox.setDefaultValue(true);
			category.addPreference(mListWidgetCheckBox);

			mContentTextOnlyCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mContentTextOnlyCheckBox.setKey(PreferenceKeys.CONTENT_TEXT_ONLY);
			mContentTextOnlyCheckBox.setTitle(R.string.text_only);
			mContentTextOnlyCheckBox.setSummary(R.string.text_only_summary);
			mContentTextOnlyCheckBox.setDefaultValue(false);
			category.addPreference(mContentTextOnlyCheckBox);

			mWebViewPluginCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mWebViewPluginCheckBox.setKey(PreferenceKeys.WEBVIEW_PLUGIN_ENABLED);
			mWebViewPluginCheckBox.setTitle(R.string.webview_plugin);
			mWebViewPluginCheckBox.setSummary(R.string.webview_plugin_summary);
			category.addPreference(mWebViewPluginCheckBox);
			
			mCloseDialogCheckBox = new CheckBoxPreference(SettingsActivity.this);
			mCloseDialogCheckBox.setKey(PreferenceKeys.CLOSE_DIALOG_ENABLED);
			mCloseDialogCheckBox.setTitle(R.string.close_dialog);
			mCloseDialogCheckBox.setSummary(R.string.close_dialog_summary);
			mCloseDialogCheckBox.setDefaultValue(true);
			category.addPreference(mCloseDialogCheckBox);
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if (preference == mAutoAlertMemoCheckBox) {
				if ((Boolean)newValue) {
					if (StringUtil.isEmpty(mPref.getString(PreferenceKeys.ACCOUNT_ID, null)))
						DialogBuilder.createAlertDialog(mActivity, getString(R.string.dialog_empty_account_message)).show();
					else
						mAutoAlertMemoCheckBox.setChecked(true);
				} else {
					mAutoAlertMemoCheckBox.setChecked(false);
				}
			}
			return false;
		}
    }

	//[ IME auto-visible
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		
		InputMethodManager inputManager = (InputMethodManager) getBaseContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (mIsDlgOn) {
			inputManager.showSoftInput(mAccountId, 0);
			inputManager.showSoftInput(mAccountPw, 0);
		
			mIsDlgOn = false;
		}
	}
	//] IME auto-visible
}
