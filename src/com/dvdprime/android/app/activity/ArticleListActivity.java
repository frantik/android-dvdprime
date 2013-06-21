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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.adapter.ArticleListPaginator;
import com.dvdprime.android.app.adapter.ArticleListAdapter;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.constants.RequestCode;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.dialog.ISetSelectValue;
import com.dvdprime.android.app.listener.OnEntityClickListener;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.ArticleManager;
import com.dvdprime.android.app.manager.MemoManager;
import com.dvdprime.android.app.model.Article;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView.OnEditorActionListener;

public class ArticleListActivity extends BaseActivity implements View.OnCreateContextMenuListener, OnClickListener
{
	private String CONTENT_ENCODING_EUC_KR = "EUC-KR";

	private DBAdapter dba;
	private PrefUtil prefs;
	private Article sArticle;
	private ArticleListAdapter adapter;
	private ArticleListPaginator paginator;
	
	private Handler mHandler;
	private Handler mIMEHandler;
	private InputMethodManager mIMManager;
    
    private String URL;

    private Activity activity;
    private ListView listView;
    private LinearLayout mSearchLayout;
    private EditText keywordText;
    private Button mSearchButton;
    
    private boolean loading;
    
    private int mCatIdx = 0;
    
    private String AUTH_CHECK;
    private String FROM_LIST = "from_list"; // ��Ͽ�û���κ��� �α���
    private String FROM_ADEL = "from_adel"; // �ۻ����κ��� �α���

    private final int CONTEXT_MENU_MEMO = 0;			// ���� ����
	private final int CONTEXT_MENU_SEARCH_BY_NICK = 1;	// �г������� �˻�
	private final int CONTEXT_MENU_SEARCH_BY_ID = 2;	// ���̵�� �˻�
	private final int CONTEXT_MENU_ADD_FILTER = 3;		// ���� �߰�
	private final int CONTEXT_MENU_DEL_FILTER = 4;		// ���� ����
	private final int CONTEXT_MENU_DELETE = 5;			// �� ����

	private static final int MENU_ARTICLE_WRITE		= 1;
    private static final int MENU_ARTICLE_SEARCH	= 2;
    private static final int MENU_ARTICLE_REFRESH	= 3;
    private static final int MENU_ARTICLE_SETTING	= 4;
    
    public ArticleListActivity()
    {
    	super(R.layout.article_list_layout);
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        activity = this;

        Intent intent = getIntent();
        if (intent != null) {
        	CurrentInfo.BBS_MAJOR = intent.getStringExtra(IntentKeys.BBS_MAJOR);
        	CurrentInfo.BBS_MINOR = intent.getStringExtra(IntentKeys.BBS_MINOR);
        	CurrentInfo.BBS_MASTER_ID = intent.getStringExtra(IntentKeys.BBS_MASTER);
        	CurrentInfo.BBS_TITLE = intent.getStringExtra(IntentKeys.BBS_TITLE);
        	CurrentInfo.BBS_URL = intent.getStringExtra(IntentKeys.BBS_URL);
        	CurrentInfo.LOGIN_CHECK = intent.getIntExtra(IntentKeys.IS_LOGIN_CHECK, 0);
        	CurrentInfo.BBS_KEYWORD = null;
        }
        
        // ����� ���� ����
        setTitle(CurrentInfo.BBS_TITLE);
        CurrentInfo.BBS_KEYWORD = null;
        
        dba = DBAdapter.getInstance();
        prefs = PrefUtil.getInstance();
        
        mHandler = new Handler();
        mIMEHandler = new Handler();
        mIMManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        
        DpUtil.updateButtonBar(this, 0);

    	// ������ �����͸� ��� ����
        dba.deleteArticleRetrieve();

    	listView = (ListView)findViewById(R.id.article_list);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnCreateContextMenuListener(this);
        
		// �� �׺� ���� ����
		setQuickNavi();
		// ��� �˻�â ����
		setSearchLayout();
		// �⺻ URL ����
		URL = Const.HOMEPAGE_URL + CurrentInfo.BBS_URL;
        
        loading = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
		// �̹� �ε��� �Ȱ�� �ε����� ����.
		if (loading) {
			load();
		}
    }
    
    @Override
    protected void onPause() {
		if (mSearchLayout.getVisibility() == View.VISIBLE) {
			mSearchLayout.setVisibility(View.GONE);
		}
		
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
		ArticleManager.getInstance().removeConsumer();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == RequestCode.REQ_CODE_ARTICLE_WRITE
				|| requestCode == RequestCode.REQ_CODE_ARTICLE_DELETE)
				&& resultCode == Activity.RESULT_OK) {
			// ���� �߻����� ������ �� ���ΰ�ħ
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					getArticleList();
				}
			}, 300);
		}
	}
	
	@Override
	public void onBackPressed() {
		// �˻�â�� ���϶��� ���ư Ŭ���� �˻�â �ݱ�
		if (mSearchLayout.getVisibility() == View.VISIBLE) {
			mSearchLayout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_top_out));
			mSearchLayout.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
    	if (info.position > -1) {
    		sArticle = (Article) adapter.getItem(info.position);
    		
			if (sArticle == null)
				return;
			
			menu.add(Menu.NONE, CONTEXT_MENU_MEMO, Menu.NONE, R.string.article_context_send_memo).setEnabled(true);
			menu.add(Menu.NONE, CONTEXT_MENU_SEARCH_BY_NICK, Menu.NONE+1, R.string.article_context_search_by_nick).setEnabled(true);
			menu.add(Menu.NONE, CONTEXT_MENU_SEARCH_BY_ID, Menu.NONE+2, R.string.article_context_search_by_id).setEnabled(true);
			
			if (StringUtil.equals(prefs.getString(PreferenceKeys.ACCOUNT_ID, null), 
					sArticle.getUserId()))
				menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE+3, R.string.article_context_delete).setEnabled(true);
			else {
				String[] fIdList = StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR);
				if (fIdList != null && StringUtil.contains(fIdList, sArticle.getUserId()))
					menu.add(Menu.NONE, CONTEXT_MENU_DEL_FILTER, Menu.NONE+3, R.string.article_context_del_filter).setEnabled(true);
				else
					menu.add(Menu.NONE, CONTEXT_MENU_ADD_FILTER, Menu.NONE+3, R.string.article_context_add_filter).setEnabled(true);
			}
    	}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case CONTEXT_MENU_MEMO:
				Intent intent = new Intent(ArticleListActivity.this, MemoWriteActivity.class);
				intent.putExtra(IntentKeys.MEMO_RECEIVER, sArticle.getUserId());
				startActivity(intent);
				break;
			case CONTEXT_MENU_SEARCH_BY_NICK:
				mCatIdx = 3;
				getArticleListByKeyword(sArticle.getUserName());
				break;
			case CONTEXT_MENU_SEARCH_BY_ID:
				mCatIdx = 1;
				getArticleListByKeyword(sArticle.getUserId());
				break;
			case CONTEXT_MENU_ADD_FILTER:
				if (prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null) == null) {
					prefs.setString(PreferenceKeys.FILTERING_ID_LIST, sArticle.getUserId());
					prefs.setString(PreferenceKeys.FILTERING_NICK_LIST, sArticle.getUserName());
				} else { 
					prefs.setString(PreferenceKeys.FILTERING_ID_LIST, 
							prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null) + Const.DEFAULT_SEPARATOR + sArticle.getUserId());
					prefs.setString(PreferenceKeys.FILTERING_NICK_LIST, 
							prefs.getString(PreferenceKeys.FILTERING_NICK_LIST, null) + Const.DEFAULT_SEPARATOR + sArticle.getUserName());
				}
				Toast.makeText(activity, getText(R.string.msg_filtering_added), Toast.LENGTH_SHORT).show();
				break;
			case CONTEXT_MENU_DEL_FILTER:
				String[] idList = StringUtil.remove(
										StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR),
										sArticle.getUserId());
				String[] nickList = StringUtil.remove(
										StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_NICK_LIST, null), Const.DEFAULT_SEPARATOR),
										sArticle.getUserName());
				prefs.setString(PreferenceKeys.FILTERING_ID_LIST, StringUtil.join(idList, Const.DEFAULT_SEPARATOR));
				prefs.setString(PreferenceKeys.FILTERING_NICK_LIST, StringUtil.join(nickList, Const.DEFAULT_SEPARATOR));

				Toast.makeText(activity, getText(R.string.msg_filtering_removed), Toast.LENGTH_SHORT).show();
				break;
			case CONTEXT_MENU_DELETE:
				DialogBuilder.createConfirmDialog(activity, getString(R.string.really_delete), 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, final int which) {
		                    	dialog.dismiss();

		        				if (!DpUtil.isAutoLoginEnabled() &&
		        						StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // ���� ������ ���� ��� ���
		        					DialogBuilder.createConfirmDialog(activity, 
		        											getString(R.string.dialog_login_check_message), 
		        											new DialogInterface.OnClickListener() {
		        												@Override
		        												public void onClick(final DialogInterface dialog, final int which) {
		        							                    	dialog.dismiss();
		        							    					AUTH_CHECK = FROM_ADEL;
		        							                    	onLoginCheck();
		        												}
		        											}).show();
		        				}
		        				else if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // ���� ������ ���� ��� ��õ�� �� ������ �˷���.
		        					DialogBuilder.createAlertDialog(activity, 
		        							getString(R.string.dialog_recommend_alert_message))
		        							.show();
		        				}
		        				else {
		        					AUTH_CHECK = FROM_ADEL;
		        					onLoginCheck();
		        				}
							}
				}).show();
				break;
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_ARTICLE_WRITE, Menu.NONE, R.string.menu_write).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, MENU_ARTICLE_SEARCH, Menu.NONE, R.string.menu_search).setIcon(android.R.drawable.ic_menu_search);
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
			case MENU_ARTICLE_WRITE:
				intent = new Intent(this, ArticleWriteActivity.class);
				startActivityForResult(intent, RequestCode.REQ_CODE_ARTICLE_WRITE);
				break;
			case MENU_ARTICLE_SEARCH:
				if (mSearchLayout.getVisibility() == View.GONE) {
					mSearchLayout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_top_in));
					mSearchLayout.setVisibility(View.VISIBLE);
					mSearchLayout.requestFocus();
					mIMEHandler.postDelayed(mIMERunnable, 300);
				}
				break;
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
		
		if (mSearchLayout.getVisibility() == View.VISIBLE) {
			mSearchLayout.setVisibility(View.GONE);
		}
		
		if (StringUtil.isNotEmpty(URL)) { 
//			if (CurrentInfo.LOGIN_CHECK == 0 || !DpUtil.isAutoLoginEnabled()) {
			// �α����� �ʿ���� �Խ���
			if (CurrentInfo.LOGIN_CHECK == 0 && !prefs.getBoolean(PreferenceKeys.ALERT_MEMO_ENABLED, false)) {
				getArticleList();
			} else if (CurrentInfo.LOGIN_CHECK == 1 &&
				StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) {
					DialogBuilder.createConfirmDialog(activity, 
						getString(R.string.dialog_require_account_message), 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, final int which) {
		                    	dialog.dismiss();
		        				Intent intent = new Intent(ArticleListActivity.this, AccountSettingActivity.class);
		        				startActivity(intent);
		        				finish();
							}
					}).show();
			} else {
				AUTH_CHECK = FROM_LIST;
				onLoginCheck();
			}
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
						if (StringUtil.equals(AUTH_CHECK, FROM_LIST))
							getArticleList();
						else if (StringUtil.equals(AUTH_CHECK, FROM_ADEL))
							onContentDelete();
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
						if (StringUtil.equals(AUTH_CHECK, FROM_LIST))
							getArticleList();
						else if (StringUtil.equals(AUTH_CHECK, FROM_ADEL))
							onContentDelete();
					break;
				case DataEvent.ARTICLE_LIST:
					super.setRetry(false, MessageHelper.RETRY_INIT);

					if (dba.getArticleList() != null) {
						adapter = new ArticleListAdapter(this,
											R.layout.article_list_item, 
											dba.getArticleList());
						
						paginator = new ArticleListPaginator();
						adapter.setPaginator(paginator);
						adapter.setLoadingViewResourceId(R.layout.article_list_footer);
						
						listView.setAdapter(adapter);
						OnEntityClickListener l = new OnEntityClickListener(this);
						listView.setOnItemClickListener(l);
						
						// �˻��� ��ȸ�� Ÿ��Ʋ ����
						if (StringUtil.isNotEmpty(CurrentInfo.BBS_KEYWORD)) {
							String title = CurrentInfo.BBS_TITLE + " ";
							title += "("+getResources().getStringArray(R.array.entries_search_category)[mCatIdx];
							title += ":"+CurrentInfo.BBS_KEYWORD+")";
							setTitle(title);
						}
						
						// ����� ���� ��쿡 �׺� ���� Ȱ��ȭ
						if (prefs.getBoolean(PreferenceKeys.WIDGET_NAVI_ENABLED, true)) {
							RelativeLayout mNeviLayout = (RelativeLayout)findViewById(R.id.article_list_quick_navi_layout);
							mNeviLayout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.transition_in));
							mNeviLayout.setVisibility(View.VISIBLE);
						}
						// ���� Ȯ�� ��û
						if (prefs.getBoolean(PreferenceKeys.ALERT_MEMO_ENABLED, false)) {
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									MemoManager.getInstance().check();
								}
							}, 300);
						}

					} else {
						listView.setAdapter(null);
					}
					loading = false;
					hideLoadingIndicator(true);
					break;
				case DataEvent.ARTICLE_DELETE:
					Toast t = Toast.makeText(activity, null, Toast.LENGTH_SHORT);
					if ((Boolean)event.getData()) {
						// ���� ���� �佺Ʈ �˸�
						t.setText(R.string.deleted);
						t.show();
						// ���� �� ��� ���û
						getArticleList();
					} else {
						// ���� ���� �佺Ʈ �˸�
						t.setText(R.string.bbs_toast_fail_message);
						t.show();
						loading = false;
						hideLoadingIndicator(true);
					}
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
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.article_list_quick_write_imageView:
				intent = new Intent(this, ArticleWriteActivity.class);
				startActivityForResult(intent, RequestCode.REQ_CODE_ARTICLE_WRITE);
				break;
			case R.id.article_list_quick_search_imageView:
				mSearchLayout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_top_in));
				mSearchLayout.setVisibility(View.VISIBLE);
				mSearchLayout.requestFocus();
				mIMEHandler.postDelayed(mIMERunnable, 300);
				break;
			case R.id.article_list_quick_refresh_imageView:
				loading = true;
				load();
				break;
		}
		
	}
	
	/**
	 * �� �׺� ���� ���� ����
	 */
	private void setQuickNavi() {
		ImageView mWriteImage = (ImageView)findViewById(R.id.article_list_quick_write_imageView);
		ImageView mSearchImage = (ImageView)findViewById(R.id.article_list_quick_search_imageView);
		ImageView mRefreshImage = (ImageView)findViewById(R.id.article_list_quick_refresh_imageView);
		
		mWriteImage.setOnClickListener(this);
		mSearchImage.setOnClickListener(this);
		mRefreshImage.setOnClickListener(this);
	}
	
	/**
	 * �˻�â ����
	 */
	private void setSearchLayout() {
		mSearchLayout = (LinearLayout) findViewById(R.id.article_list_search_layout);
		// ī�װ� ���� ��ư
		final Button categoryBtn = (Button) findViewById(R.id.article_list_category_button);
		categoryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listView.requestFocus();
				DialogBuilder.createSelectDialog(activity, 
						getString(R.string.search_category_title), 
						getResources().getStringArray(R.array.entries_search_category), mCatIdx, 
						new ISetSelectValue(){
							@Override
							public void setValue(String value, int index) {
								mCatIdx = index;
								if (mCatIdx == 2) {
									((EditText) findViewById(R.id.article_list_keyword_editText)).setText(R.string.empty);
							        final Calendar c = Calendar.getInstance();
							        new DatePickerDialog(activity,
				                            mDateSetListener,
				                            c.get(Calendar.YEAR), 
				                            c.get(Calendar.MONTH), 
				                            c.get(Calendar.DAY_OF_MONTH)).show();
								}
								else if (mCatIdx == 6) {
									if (keywordText.getText().length() > 0)
										SendButtonSetEnabled(true);
								}
								else if (keywordText.getText().length() < 2)
									SendButtonSetEnabled(false);
								
								((Button) findViewById(R.id.article_list_category_button))
									.setText(getResources().getTextArray(R.array.entries_search_category)[index]);
							}
						}
				).show();
			}
		});
		// �˻� Ű���� �Է�â
		keywordText = (EditText) findViewById(R.id.article_list_keyword_editText);
		keywordText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE 
						|| actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_SEARCH) {
		
					clickSearchButton();				
				}
				return false;
			}
		});
		keywordText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (mCatIdx == 2 && hasFocus) {
			        final Calendar c = Calendar.getInstance();
			        new DatePickerDialog(activity,
                            mDateSetListener,
                            c.get(Calendar.YEAR), 
                            c.get(Calendar.MONTH), 
                            c.get(Calendar.DAY_OF_MONTH)).show();
				}
			}
		});
		keywordText.addTextChangedListener(mKeywordTextWatcher);
		// �˻� ��ư
		mSearchButton = (Button) findViewById(R.id.article_list_search_btn);
		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickSearchButton();
			}
		});
		SendButtonSetEnabled(false);
	}
	
	/**
	 * �α��� ����
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
	 * ���� ��û ����
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
	 * �Խù� ��� ��û
	 */
	private void getArticleList() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			super.setRetry(true, MessageHelper.RETRY_GET);
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_LIST);
			am.list(URL);
			showLoadingIndicator(R.string.bbs_progressbar_loading);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}
	
	/**
	 * �˻� �̺�Ʈ ��û
	 */
	private void clickSearchButton() {
		Toast t = Toast.makeText(activity, null, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.TOP, 0, 120);
		if (mCatIdx == 6)
			if (StringUtil.isNumber(keywordText.getText().toString()))
				getArticleListByPage(keywordText.getText().toString());
			else {
				t.setText(R.string.search_keyword_include_string_message);
				t.show();
				mIMEHandler.postDelayed(mIMERunnable, 300);
			}
		else if (keywordText.getText().length() > 1)
			getArticleListByKeyword(keywordText.getText().toString());
		else {
			t.setText(R.string.search_keyword_too_short_message);
			t.show();
			mIMEHandler.postDelayed(mIMERunnable, 300);
		}
	}
	
	/**
	 * �Խù� �˻� ��û
	 */
	private void getArticleListByKeyword(String keyword) {
		if (SystemUtil.isNetworkAvailable(activity)) {
			try {
				// Ű���� �����
				closeSoftKeyboard();
				// �˻�â �����
				mSearchLayout.setVisibility(View.GONE);
				// �˻��� ����
				CurrentInfo.BBS_KEYWORD = keyword;
				// URL �����ϱ�
				String searchUrl = URL + StringUtil.replace(Const.SEARCH_OPTION_URL, "SearchCondition=", 
						"SearchCondition="+getResources().getStringArray(R.array.entryvalues_search_category)[mCatIdx]);
				searchUrl += URLEncoder.encode(keyword, CONTENT_ENCODING_EUC_KR);
				// �˻� ��� ��û
				ArticleManager am = ArticleManager.getInstance();
				am.addConsumer(this, DataEvent.ARTICLE_LIST);
				am.list(searchUrl);
				showLoadingIndicator(R.string.bbs_progressbar_loading);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}

	/**
	 * ������ �̵� ��û
	 */
	private void getArticleListByPage(String page) {
		if (SystemUtil.isNetworkAvailable(activity)) {
			// Ű���� �����
			closeSoftKeyboard();
			// �˻�â �����
			mSearchLayout.setVisibility(View.GONE);
			// URL �����ϱ�
			String searchUrl = URL + "&Page=" + page;
			// �˻� ��� ��û
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_LIST);
			am.list(searchUrl);
			showLoadingIndicator(R.string.bbs_progressbar_loading);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}

	/**
	 * ���� ����
	 */
	private void onContentDelete() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			String bbsId = StringUtil.substringBefore(StringUtil.substringAfter(sArticle.getUrl(), "bbslist_id="), "&page");
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_DELETE);
			am.delete(CurrentInfo.BBS_MAJOR, CurrentInfo.BBS_MINOR, CurrentInfo.BBS_MASTER_ID, bbsId);
			showLoadingIndicator(R.string.bbs_progressbar_deleting);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                // Month is 0 based so add 1
            	String mMon = (monthOfYear+1) < 10 ? "0"+(monthOfYear+1):""+(monthOfYear+1);
            	String mDay  = dayOfMonth < 10 ? "0"+dayOfMonth:""+dayOfMonth;
            	keywordText.setText(
                        new StringBuilder()
                        		.append(year).append("-")
                                .append(mMon).append("-")
                                .append(mDay));
            	listView.requestFocus();
            }
        };

	private TextWatcher mKeywordTextWatcher = new TextWatcher() {
    	
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			onUserInteraction();
            
        	if(!mSearchButton.isEnabled()) {
        		if(s.length() > 1)
        			SendButtonSetEnabled(true);
        	} else {
        		if(s.length() < 2)
        			SendButtonSetEnabled(false);
        	}
        	// �������� ��� ���� ó��
        	if (mCatIdx == 6 && !mSearchButton.isEnabled()) {
        		if(s.length() > 0)
        			SendButtonSetEnabled(true);
        	} else {
        		if(s.length() < 1)
        			SendButtonSetEnabled(false);
        	}
		}

		public void afterTextChanged(Editable s) {
		}
	};

	/**
	 * Ű���� ����� 
	 */
	private void closeSoftKeyboard() {
		EditText editText = (EditText) findViewById(R.id.article_list_keyword_editText);
		mIMManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * Ű���� ���̱�
	 */
	private Runnable mIMERunnable = new Runnable()
	{
		public void run()
		{
			mIMManager.showSoftInput(keywordText, 0);
		}
	};

    private void SendButtonSetEnabled(boolean value){
    	mSearchButton.setEnabled(value);
    	if( value == true ){
    		mSearchButton.setShadowLayer(0.3f, -1, -1, Color.parseColor("#80000000"));
    	}else{
    		mSearchButton.setShadowLayer(0, 0, 0, Color.parseColor("#80000000"));
    	}    	
    }
}