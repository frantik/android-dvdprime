package com.dvdprime.android.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.MemoManager;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

public class MemoWriteActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private Activity activity;
	private InputMethodManager mIMManager;
	
	private EditText mReceiverText;
	private EditText mContentText;
	private CheckBox mSendCheck;
	private Button mSendButton;
	
	private Toast toast;
	private PrefUtil prefs;
	
	private String mSendSave;
	private String memoReceiver;
	
	public MemoWriteActivity() {
		super(R.layout.memo_write_layout);
	}
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			memoReceiver = extras.getString(IntentKeys.MEMO_RECEIVER);
		}
		
		setTitle(R.string.memo_write_title);

		activity = this;
		prefs = PrefUtil.getInstance();
		toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 150);
		mIMManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mReceiverText = (EditText)findViewById(R.id.memo_write_title_editText);
		mReceiverText.addTextChangedListener(mReceiverWatcher);
		mContentText = (EditText)findViewById(R.id.memo_write_content_editText);
		mContentText.addTextChangedListener(mContentWatcher);
		
		mSendCheck = (CheckBox)findViewById(R.id.memo_write_send_box_checkBox);
		mSendCheck.setOnCheckedChangeListener(this);
		
		mSendButton = (Button)findViewById(R.id.memo_write_softkey_left_button);
		mSendButton.setOnClickListener(this);
		
		Button mCancelButton = (Button)findViewById(R.id.memo_write_softkey_right_button);
		mCancelButton.setOnClickListener(this);
		
		SendButtonSetEnabled(false);
		
		// 받는 사람 정보 설정
		if (StringUtil.isNotEmpty(memoReceiver))
			mReceiverText.setText(memoReceiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null)))
			DialogBuilder.showCriticalErrorMessageDialog(this, 
					null, true, R.string.dialog_comment_alert_message);
	}

	@Override
	protected void onPause() {
		
		MemoManager.getInstance().removeConsumer();
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		
		MemoManager.getInstance().removeConsumer();
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
    }

	@Override
	public final void handleEvent(final DataEvent event) {
		if (MessageHelper.DEBUG)
			Log.d("DP", this.getLocalClassName()+".handleEvent(event):"+event.getType());
		
		if (event.getData() != null) {
			switch (event.getType()) {
			case DataEvent.LOGIN:
				if ((Integer)event.getData() == Const.OK) {
					onMemoWrite();
				} else {
					hideLoadingIndicator(true);
					DialogBuilder.createAlertDialog(activity, getString(R.string.login_failed_message))
								.show();
				}
				break;
			case DataEvent.LOGIN_CHECK:
				boolean chk = (Boolean)event.getData(); 
				if (!chk)
					onLogin();
				else
					onMemoWrite();
				break;
			case DataEvent.MEMO_WRITE:
				if ((Boolean)event.getData()) {
					Toast.makeText(getBaseContext(), getString(R.string.memo_send_success_message), Toast.LENGTH_SHORT).show();
					hideLoadingIndicator(true);
					finish();
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.memo_send_fail_message), Toast.LENGTH_SHORT).show();
					hideLoadingIndicator(true);
				}
				break;
			}
		} else {
			hideLoadingIndicator(true);
			super.handleEvent(event);
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked)
			mSendSave = "1";
		else
			mSendSave = null;
	}
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.memo_write_softkey_left_button:
			closeSoftKeyboard();
			if (SystemUtil.isNetworkAvailable(activity)) {
				AccountManager aMng = AccountManager.getInstance();
				aMng.addConsumer(this, DataEvent.LOGIN_CHECK);
				aMng.loginCheck();
				showLoadingIndicator(R.string.bbs_progressbar_checking);
			} else {
				DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
														false,
														R.string.network_problem);													
			}
			break;
		case R.id.memo_write_softkey_right_button:
			finish();
			break;
		}
	}

	/**
	 * 로그인 실행
	 */
	private void onLogin() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			AccountManager aMng = AccountManager.getInstance();
			aMng.addConsumer(this, DataEvent.LOGIN);
			aMng.login(prefs.getString(PreferenceKeys.ACCOUNT_ID, ""), 
					prefs.getString(PreferenceKeys.ACCOUNT_PW, ""));
			showLoadingIndicator(R.string.bbs_progressbar_login);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 글쓰기 전송
	 */
	private void onMemoWrite() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			MemoManager mm = MemoManager.getInstance();
			mm.addConsumer(this, DataEvent.MEMO_WRITE);
			mm.write(mReceiverText.getText().toString(), 
					mContentText.getText().toString(), 
					mSendSave);
			showLoadingIndicator(R.string.bbs_progressbar_save);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	private void SendButtonSetEnabled(boolean value){
    	mSendButton.setEnabled(value);
    	if( value == true ){
    		mSendButton.setShadowLayer(0.3f, -1, -1, Color.parseColor("#80000000"));
    	}else{
    		mSendButton.setShadowLayer(0, 0, 0, Color.parseColor("#80000000"));
    	}    	
    }

	private void closeSoftKeyboard() {
		mIMManager.hideSoftInputFromWindow(mContentText.getWindowToken(), 0);
	}

	private TextWatcher mReceiverWatcher = new TextWatcher() {

		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			onUserInteraction();
			
			if (mContentText.getText().length() > 0) {
	        	if(!mSendButton.isEnabled()) {
	        		if(s.length() > 0)
	        			mSendButton.setEnabled(true);
	        	} else {
	        		if(s.length() == 0)
	        			mSendButton.setEnabled(false);
	        	}
			} else
				mSendButton.setEnabled(false);
		}

        public void afterTextChanged(Editable s) { }
    };
    
	private TextWatcher mContentWatcher = new TextWatcher() {

		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			onUserInteraction();
            
			if (mReceiverText.getText().length() > 0) {
	        	if(!mSendButton.isEnabled()) {
	        		if(s.length() > 0)
	        			mSendButton.setEnabled(true);
	        	} else {
	        		if(s.length() == 0)
	        			mSendButton.setEnabled(false);
	        	}
			} else
				mSendButton.setEnabled(false);

        	int length = s.length();
			final int CONTENT_MAX_LENGTH = 500;
        	if(length > CONTENT_MAX_LENGTH) { //exceed 500 chars in a message
        		mContentText.setText(s.subSequence(0, CONTENT_MAX_LENGTH-1));

        		toast.setText(R.string.memo_exceed_max_content_length);
        		toast.show();
        		mSendButton.setEnabled(false);
        	}
        	if (StringUtil.contains(s.toString(), "|") || StringUtil.contains(s.toString(), "#")) {
        		toast.setText(R.string.memo_content_contain_special_char);
        		toast.show();
        		
        		String text = StringUtil.replace(s.toString(), '|', "");
        		text = StringUtil.replace(s.toString(), '#', "");
        		mContentText.setText(text);
        	}
        		
		}

        public void afterTextChanged(Editable s) { }
    };

}
