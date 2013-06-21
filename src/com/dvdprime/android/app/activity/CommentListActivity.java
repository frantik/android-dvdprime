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
import com.dvdprime.android.app.adapter.ScrapListAdapter;
import com.dvdprime.android.app.adapter.ScrapListPaginator;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.listener.OnEntityClickListener;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.ArticleManager;
import com.dvdprime.android.app.manager.ScrapManager;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class CommentListActivity extends BaseActivity 
{
	private DBAdapter dba;
	private PrefUtil prefs;
	private ScrapListAdapter adapter;
	private ScrapListPaginator paginator;
	
    private Activity activity;
    private ListView listView;
    
    private boolean loading;
    
    private static final int MENU_ARTICLE_REFRESH	= 1;
    private static final int MENU_ARTICLE_SETTING	= 2;
    
    public CommentListActivity()
    {
    	super(R.layout.article_list_layout);
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        activity = this;

        dba = DBAdapter.getInstance();
        prefs = PrefUtil.getInstance();
        
        DpUtil.updateButtonBar(this, 0);

        // 기존의 데이터를 모두 삭제
        dba.deleteScrapRetrieve();
        
        // 제목 설정
        setTitle(R.string.list_comment);

    	listView = (ListView)findViewById(R.id.article_list);
        listView.setEmptyView(findViewById(R.id.empty));
        
        loading = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
		if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null)))
			DialogBuilder.showCriticalErrorMessageDialog(this, 
					null, true, R.string.dialog_comment_alert_message);
		else {
			// 이미 로딩이 된경우 로딩하지 않음.
			if (loading) {
				load();
			}
		}
    }
    
    @Override
    protected void onPause() {
		ArticleManager.getInstance().removeConsumer();
        super.onPause();
    }
    
	@Override
	protected void onDestroy() {
	    // Because we pass the adapter to the next activity, we need to make
	    // sure it doesn't keep a reference to this activity. We can do this
	    // by clearing its DatasetObservers, which setListAdapter(null) does.
        listView.setAdapter(null);
        adapter = null;

        super.onDestroy();
    }
    
	@Override
	public final void invalidate() {
		super.invalidate();

		if (paginator != null) {
			paginator.invalidate();
		}
		ScrapManager.getInstance().removeConsumer();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_ARTICLE_REFRESH, Menu.NONE, R.string.menu_refresh).setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, MENU_ARTICLE_SETTING, Menu.NONE, R.string.menu_setting).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		Intent intent;
		switch (item.getItemId()) {
			case MENU_ARTICLE_REFRESH:
				loading = true;
				load();
				break;
			case MENU_ARTICLE_SETTING:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		
		return false;
	}


	@Override
	public final void load() {
		super.load();
		
		// 로그인 정보 확인
		if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) {
				DialogBuilder.createConfirmDialog(activity, 
					getString(R.string.dialog_require_account_message), 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int which) {
	                    	dialog.dismiss();
	        				Intent intent = new Intent(CommentListActivity.this, AccountSettingActivity.class);
	        				startActivity(intent);
	        				finish();
						}
				}).show();
		} else {
			onLoginCheck();
		}
	}
	
	@Override
	public final void handleEvent(final DataEvent event) {
		if (MessageHelper.DEBUG)
			Log.d("DP", this.getLocalClassName()+".handleEvent(event):"+event.getType());
		
		if (event.getData() != null) {
			switch (event.getType()) {
				case DataEvent.LOGIN:
					if ((Integer)event.getData() == Const.OK)
						getCommentList();
					else {
						loading = false;
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
						getCommentList();
					break;
				case DataEvent.COMMENT_LIST:
					super.setRetry(false, MessageHelper.RETRY_INIT);

					if (dba.getScrapList(Const.TYPE_CMT) != null) {
						adapter = new ScrapListAdapter(this,
											R.layout.article_list_item, 
											dba.getScrapList(Const.TYPE_CMT));
						
						paginator = new ScrapListPaginator();
						paginator.setType(Const.TYPE_CMT);
						adapter.setPaginator(paginator);
						adapter.setLoadingViewResourceId(R.layout.article_list_footer);
						
						listView.setAdapter(adapter);
						OnEntityClickListener l = new OnEntityClickListener(this);
						listView.setOnItemClickListener(l);
					} else {
						listView.setAdapter(null);
					}
					loading = false;
					hideLoadingIndicator(true);
					break;
				default:
					loading = false;
					hideLoadingIndicator(true);
					break;
			}
		} else {
			loading = false;
			super.handleEvent(event);
		}
	}
	
	/**
	 * 로그인 실행
	 */
	private void onLogin() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			AccountManager aMng = AccountManager.getInstance();
			aMng.addConsumer(this, DataEvent.LOGIN);
			aMng.login(prefs.getString(PreferenceKeys.ACCOUNT_ID, ""), prefs.getString(PreferenceKeys.ACCOUNT_PW, ""));
			showLoadingIndicator(R.string.bbs_progressbar_login);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 권한 요청 실행
	 */
	private void onLoginCheck() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			AccountManager aMng = AccountManager.getInstance();
			aMng.addConsumer(this, DataEvent.LOGIN_CHECK);
			aMng.loginCheck();
			showLoadingIndicator(R.string.bbs_progressbar_checking);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 댓글창고 목록 요청
	 */
	private void getCommentList() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			super.setRetry(true, MessageHelper.RETRY_GET);
			ScrapManager sm = ScrapManager.getInstance();
			sm.addConsumer(this, DataEvent.COMMENT_LIST);
			sm.commentList(Const.MY_COMMENT_URL);
			showLoadingIndicator(R.string.bbs_progressbar_loading);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}
}