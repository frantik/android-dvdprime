package com.dvdprime.android.app.activity;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.adapter.MemoListAdapter;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.MemoManager;
import com.dvdprime.android.app.model.Memo;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

public class MemoListActivity extends BaseActivity implements View.OnCreateContextMenuListener
{

	private DBAdapter dba;
	private PrefUtil prefs;
	private MemoListAdapter adapter;

    private Activity activity;
    private ListView listView;
    
    private Memo sMemo;

    private String URL;
    private String AUTH_CHECK;
    private int TYPE;
    private boolean loading;
    
	private final int CONTEXT_MENU_STORAGE = 0; // 보관
	private final int CONTEXT_MENU_REPLY   = 1; // 답장
	private final int CONTEXT_MENU_DELETE  = 2;	// 삭제
	
    private String FROM_LIST = "from_list"; // 목록 호출로부터 로그인
    private String FROM_STRG = "from_strg"; // 저장으로부터 로그인
    private String FROM_MDEL = "from_mdel"; // 삭제로부터 로그인

    private static final int MENU_MEMO_WRITE	= 1; // 쪾지 작성
    private static final int MENU_MEMO_REFRESH	= 2; // 쪾지 작성
    private static final int MENU_MEMO_SETTING	= 3; // 설정

	public MemoListActivity() {
		super(R.layout.memo_list_layout);
	}

    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			TYPE = extras.getInt(IntentKeys.MEMO_TYPE);
		}

		activity = this;
        dba = DBAdapter.getInstance();
        prefs = PrefUtil.getInstance();
        
    	// 기존의 데이터를 모두 삭제
        dba.deleteMemoRetrieve();

    	listView = (ListView)findViewById(R.id.memo_list_listView);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnCreateContextMenuListener(this);
        
		LayoutInflater inflater = LayoutInflater.from(this);
		listView.addFooterView(inflater.inflate(R.layout.memo_list_footer, null));
        
        switch(TYPE) {
        case MessageHelper.MEMO_R:
        	URL = Const.MEMO_RECEIVED_URL;
        	break;
        case MessageHelper.MEMO_S:
        	URL = Const.MEMO_SEND_URL;
        	break;
        case MessageHelper.MEMO_X:
        	URL = Const.MEMO_STORAGE_URL;
        	break;
        }
        
        CurrentInfo.MEMO_PAGE_NOW = 1;
        loading = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
		if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null)))
			DialogBuilder.showCriticalErrorMessageDialog(this, 
					null, true, R.string.dialog_comment_alert_message);
		else {
			// 이미 로딩이 된 경우 로딩하지 않음.
			if (loading) {
				load();
			}
		}
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
	public final void load() {
		super.load();
		AUTH_CHECK = FROM_LIST;
		onLoginCheck();
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
							getMemoList();
						else if (StringUtil.equals(AUTH_CHECK, FROM_STRG))
							onMemoStorage();
						else if (StringUtil.equals(AUTH_CHECK, FROM_MDEL))
							onMemoDelete();
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
							getMemoList();
						else if (StringUtil.equals(AUTH_CHECK, FROM_STRG))
							onMemoStorage();
						else if (StringUtil.equals(AUTH_CHECK, FROM_MDEL))
							onMemoDelete();
					break;
				case DataEvent.MEMO_LIST:
					super.setRetry(false, MessageHelper.RETRY_INIT);

					if (dba.getMemoList() != null) {
						adapter = new MemoListAdapter(this,
											R.layout.memo_list_row, 
											dba.getMemoList());
						
						listView.setAdapter(adapter);
						
						// 목록이 있을 경우 페이지를 표시해준다.
						setPageNavigator();
					} else {
						listView.setAdapter(null);
					}
					// 쪽지 개수 초기화
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					mNotificationManager.cancel(R.string.alert_memo);
					prefs.setInt(PreferenceKeys.NEW_MEMO_COUNT, 0);
					
					loading = false;
					hideLoadingIndicator(true);
					break;
				case DataEvent.MEMO_STORAGE:
					if ((Boolean)event.getData()) {
						Toast.makeText(activity, getString(R.string.memo_success_storage_message), Toast.LENGTH_SHORT).show();
						AUTH_CHECK = FROM_LIST;
						onLoginCheck();
					} else {
						Toast.makeText(activity, getString(R.string.memo_fail_storage_message), Toast.LENGTH_SHORT).show();
						loading = false;
						hideLoadingIndicator(true);
					}
					break;
				case DataEvent.MEMO_DELETE:
					if ((Boolean)event.getData()) {
						Toast.makeText(activity, getString(R.string.memo_success_delete_message), Toast.LENGTH_SHORT).show();
						AUTH_CHECK = FROM_LIST;
						onLoginCheck();
					} else {
						Toast.makeText(activity, getString(R.string.memo_fail_delete_message), Toast.LENGTH_SHORT).show();
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
    	if (info.position > -1) {
    		sMemo = (Memo) adapter.getItem(info.position);
			
			if (sMemo == null)
				return;
			
			if (TYPE == MessageHelper.MEMO_R)
				menu.add(Menu.NONE, CONTEXT_MENU_STORAGE, Menu.NONE, R.string.memo_context_menu_storage).setEnabled(true);
			if (TYPE != MessageHelper.MEMO_S)
				menu.add(Menu.NONE, CONTEXT_MENU_REPLY, Menu.NONE+1, R.string.memo_context_menu_reply).setEnabled(true);

			menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE+1, R.string.memo_context_menu_delete).setEnabled(true);
    	}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case CONTEXT_MENU_STORAGE:
				AUTH_CHECK = FROM_STRG;
				onLoginCheck();
				break;
			case CONTEXT_MENU_REPLY:
				Intent intent = new Intent(MemoListActivity.this, MemoWriteActivity.class);
				intent.putExtra(IntentKeys.MEMO_RECEIVER, sMemo.getUserId());
				startActivity(intent);
				break;
			case CONTEXT_MENU_DELETE:
				AUTH_CHECK = FROM_MDEL;
				onLoginCheck();
				break;
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_MEMO_WRITE, Menu.NONE, R.string.menu_write_memo).setIcon(android.R.drawable.ic_menu_upload);
		menu.add(Menu.NONE, MENU_MEMO_REFRESH, Menu.NONE, R.string.menu_refresh).setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, MENU_MEMO_SETTING, Menu.NONE, R.string.menu_setting).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		Intent intent;
		switch (item.getItemId()) {
			case MENU_MEMO_WRITE:
				intent = new Intent(this, MemoWriteActivity.class);
				startActivity(intent);
				break;
			case MENU_MEMO_REFRESH:
				loading = true;
				load();
				break;
			case MENU_MEMO_SETTING:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		
		return false;
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
	 * 쪽지 목록 요청
	 */
	private void getMemoList() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			MemoManager mm = MemoManager.getInstance();
			mm.addConsumer(this, DataEvent.MEMO_LIST);
			mm.list(URL);
			showLoadingIndicator(R.string.bbs_progressbar_loading);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													true,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 쪽지 저장 요청
	 */
	private void onMemoStorage() {
		String pageFlag = "rv";
		
		if (TYPE == MessageHelper.MEMO_S)
			pageFlag = "sd";
		else if (TYPE == MessageHelper.MEMO_X)
			pageFlag = "sv";
		
		if (SystemUtil.isNetworkAvailable(activity)) {
			MemoManager mm = MemoManager.getInstance();
			mm.addConsumer(this, DataEvent.MEMO_STORAGE);
			mm.moveStorage(sMemo.getMemoId(), pageFlag);
			showLoadingIndicator(R.string.bbs_progressbar_save);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 쪽지 삭제 요청
	 */
	private void onMemoDelete() {
		String pageFlag = "rv";
		
		if (TYPE == MessageHelper.MEMO_S)
			pageFlag = "sd";
		else if (TYPE == MessageHelper.MEMO_X)
			pageFlag = "sv";
		
		if (SystemUtil.isNetworkAvailable(activity)) {
			MemoManager mm = MemoManager.getInstance();
			mm.addConsumer(this, DataEvent.MEMO_DELETE);
			mm.delete(sMemo.getMemoId(), pageFlag);
			showLoadingIndicator(R.string.bbs_progressbar_deleting);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 페이지 표시
	 */
	private void setPageNavigator() {
		RelativeLayout memoFooterLayout = (RelativeLayout)findViewById(R.id.memo_list_footer_layout);
		TextView prevPageText = (TextView)findViewById(R.id.memo_list_footer_prev_textView);
		TextView firstPageText = (TextView)findViewById(R.id.memo_list_footer_1st_textView);
		TextView secondPageText = (TextView)findViewById(R.id.memo_list_footer_2nd_textView);
		TextView thirdPageText = (TextView)findViewById(R.id.memo_list_footer_3rd_textView);
		TextView nextPageText = (TextView)findViewById(R.id.memo_list_footer_next_textView);
		
		int tmpRest = CurrentInfo.MEMO_PAGE_NOW % 3;
		int firstPageNo = CurrentInfo.MEMO_PAGE_NOW;
		if (tmpRest == 0)
			firstPageNo = CurrentInfo.MEMO_PAGE_NOW - 2;
		else if (tmpRest == 2)
			firstPageNo = CurrentInfo.MEMO_PAGE_NOW - 1;
		
		// 1페이지 하나 뿐이면 네비게이션을 숨김
		if (CurrentInfo.MEMO_PAGE_NOW == 1 && CurrentInfo.MEMO_PAGE_CNT == 1) {
			memoFooterLayout.setVisibility(View.GONE);
			return;
		}
		// 테마 설정
		if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
			memoFooterLayout.setBackgroundColor(Color.BLACK);
			prevPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleWhite);
			firstPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleWhite);
			secondPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleWhite);
			thirdPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleWhite);
			nextPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleWhite);
		} else {
			memoFooterLayout.setBackgroundColor(Color.WHITE);
			prevPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleBlack);
			firstPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleBlack);
			secondPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleBlack);
			thirdPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleBlack);
			nextPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleBlack);
		}
		// 이전목록 링크 설정
		if (CurrentInfo.MEMO_PAGE_NOW > 3) {
			final int prefPageNo = firstPageNo - 1;
			prevPageText.setClickable(true);
			prevPageText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setRequestUrl(prefPageNo);
					getMemoList();
				}
			});
		} else {
			prevPageText.setClickable(false);
			prevPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleGray);
		}
		// 첫페이지 링크 설정
		final int no1 = firstPageNo;
		firstPageText.setText(String.valueOf(no1));
		firstPageText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setRequestUrl(no1);
				getMemoList();
			}
		});
		// 두번째 페이지 링크 설정
		if (CurrentInfo.MEMO_PAGE_CNT > 1) {
			final int no2 = firstPageNo + 1;
			secondPageText.setText(String.valueOf(no2));
			secondPageText.setClickable(true);
			secondPageText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setRequestUrl(no2);
					getMemoList();
				}
			});
		} else {
			secondPageText.setClickable(false);
		}
		// 세번째 페이지 링크 설정
		if (CurrentInfo.MEMO_PAGE_CNT > 2) {
			final int no3 = firstPageNo + 2;
			thirdPageText.setText(String.valueOf(no3));
			thirdPageText.setClickable(true);
			thirdPageText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setRequestUrl(no3);
					getMemoList();
				}
			});
		} else {
			thirdPageText.setClickable(false);
		}
		// 다음목록 링크 설정
		if (CurrentInfo.MEMO_NEXT_EXIST) {
			final int nextPageNo = firstPageNo + 3;
			nextPageText.setClickable(true);
			nextPageText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setRequestUrl(nextPageNo);
					getMemoList();
				}
			});
		} else {
			nextPageText.setClickable(false);
			nextPageText.setTextAppearance(activity, R.style.CommonTextAppearanceTitleGray);
		}
	}
	
	/**
	 * 요청할 URL을 설정해 준다.
	 */
	private void setRequestUrl(int pageNo) {
		// 현재 페이지도 함께 변경해준다.
    	CurrentInfo.MEMO_PAGE_NOW = pageNo;

    	switch(TYPE) {
        case MessageHelper.MEMO_R:
        	URL = Const.MEMO_RECEIVED_URL + "&page=" + pageNo;
        	break;
        case MessageHelper.MEMO_S:
        	URL = Const.MEMO_SEND_URL + "&page=" + pageNo;
        	break;
        case MessageHelper.MEMO_X:
        	URL = Const.MEMO_STORAGE_URL + "&page=" + pageNo;
        	break;
        }
	}

}
