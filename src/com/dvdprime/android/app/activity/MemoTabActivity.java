package com.dvdprime.android.app.activity;

import com.dvdprime.android.app.BaseApplication;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.MessageHelper;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MemoTabActivity extends TabActivity implements TabHost.OnTabChangeListener {

	public static final String TAG = "MemoTabActivity";
	
	private TabHost tabHost;
	
	public static final String TAB_RECEIVED		= "received";
	public static final String TAB_SEND			= "send";
	public static final String TAB_STORAGE		= "storage";
	
	public static final int TAB_RECEIVCED_INDEX = 0;
	public static final int TAB_SEND_INDEX		= 1;
	public static final int TAB_STORAGE_INDEX	= 2;
	
	public MemoTabActivity() {
		super();
	}
	
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_host);
		
		initContent();
		
		// 전역변수에 탭호스트 설정.
		BaseApplication app = (BaseApplication) getApplication();
		app.setTabWidgetActivity(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			setCurrentTab(extras.getInt(IntentKeys.TAB_INDEX));
		}
	}
	

	private final void initContent() {
		
		tabHost = getTabHost();
		TabSpec tab = tabHost.newTabSpec(TAB_RECEIVED);
		View indicator = View.inflate(this, R.layout.tab, null);
		TextView text = (TextView) indicator.findViewById(R.id.tabTitle);
		text.setText(R.string.memo_tab_title_received);
		tab.setIndicator(indicator);
		
		Intent memoReceived = new Intent(this, MemoListActivity.class);
		memoReceived.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		memoReceived.putExtra(IntentKeys.MEMO_TYPE, MessageHelper.MEMO_R);
		tab.setContent(memoReceived);
		tabHost.addTab(tab);
		
		tab = tabHost.newTabSpec(TAB_SEND);
		indicator = View.inflate(this, R.layout.tab, null);
		text = (TextView) indicator.findViewById(R.id.tabTitle);
		text.setText(R.string.memo_tab_title_send);
		tab.setIndicator(indicator);
		
		Intent memoSend = new Intent(this, MemoListActivity.class);
		memoSend.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		memoSend.putExtra(IntentKeys.MEMO_TYPE, MessageHelper.MEMO_S);
		tab.setContent(memoSend);
		tabHost.addTab(tab);
		
		tab = tabHost.newTabSpec(TAB_STORAGE);
		indicator = View.inflate(this, R.layout.tab, null);
		text = (TextView) indicator.findViewById(R.id.tabTitle);
		text.setText(R.string.memo_tab_title_storage);
		tab.setIndicator(indicator);
		
		Intent memoStorage = new Intent(this, MemoListActivity.class);
		memoStorage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		memoStorage.putExtra(IntentKeys.MEMO_TYPE, MessageHelper.MEMO_X);
		tab.setContent(memoStorage);
		tabHost.addTab(tab);
		
		tabHost.setOnTabChangedListener(this);
	}
	
	/**
	 * Sets the current tab.
	 * 
	 * @param tabIndex
	 *            the new current tab
	 */
	private final void setCurrentTab(final int tabIndex) {
		getTabHost().setCurrentTab(tabIndex);
	}
	
	public final void hideVirtualKeyboard() {
		if (tabHost != null) {
			InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			in.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		Activity activity = getLocalActivityManager().getActivity(tabId);
        if (activity != null) {
            activity.onWindowFocusChanged(true);
        }
        
        hideVirtualKeyboard();
	}
}
