package com.dvdprime.android.app.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.AdView.OnAdLoadedListener;
import net.daum.adam.publisher.impl.AdError;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Browser;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.adapter.CommentListAdapter;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.constants.RequestCode;
import com.dvdprime.android.app.db.DBAdapter;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.ArticleManager;
import com.dvdprime.android.app.manager.CommentManager;
import com.dvdprime.android.app.manager.ImageManager;
import com.dvdprime.android.app.manager.MemoManager;
import com.dvdprime.android.app.model.Comment;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;
import com.dvdprime.android.app.view.WebImageView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;

public class ArticleContentActivity extends BaseActivity {
	
	private Toast t;
	private WebView webView;
	private TextView contentTextView;
    private ListView listView;
	private Activity activity;
	private Handler mHandler;
	private InputMethodManager mIMManager;
	private Button mSendButton;
	private DialogInterface childDialog;
	
	private DBAdapter dba;
	private PrefUtil prefs;
	private CommentListAdapter adapter;

    private final String mimeType = "TEXT/HTML";
//    private final String encoding = "EUC-KR";
    private final String encoding = "UTF-8";
//    private final String encoding = "UTF-16";
    private String URL = "";
    private String TITLE = "";
    private String BBS_ID;
    private String USER_ID;
    private String REG_DATE;
    private String COMMENT; // 댓글 작성시 내용 저장
    
    private String articleId;	// 선택된 게시물 아이디
    private String scrapType;	// 스크랩의 종류 (Const에 정의됨)
    private String commentId;	// 댓글창고에서 클릭시 해당 댓글로 이동
    private String fromShare;	// 공유하기 정보
    
    private boolean loading;
    private boolean isCmtRefresh; // 댓글쓰기 후 새로고침 일 경우 true
    private boolean isChdRefresh; // 덧글쓰기 후 새로고침 일 경우 true
    private boolean isScrapCmmt;  // 댓글창고에서 클릭시 true
    
    private Comment sComment;
    
    private Uri urlToDownload;
    private String fileName;
    private String filePath;
    
    private AdView adView1;
    private com.google.ads.AdView adView2;
    
    private String AUTH_CHECK;
    private String FROM_RCMD = "from_rcmd"; // 추천으로부터 로그인
    private String FROM_WCMT = "from_wcmt"; // 댓글쓰기로부터 로그인
    private String FROM_CCMT = "from_ccmt"; // 덧글쓰기로부터 로그인
    private String FROM_CRMD = "from_crmd"; // 댓글추천으로부터 로그인
    private String FROM_CDEL = "from_cdel"; // 댓글삭제로부터 로그인
    private String FROM_ADEL = "from_adel"; // 본문삭제로부터 로그인
    private String FROM_MYDP = "from_mydp"; // MY디피에 저장으로부터 로그인
    
	private final int CONTEXT_MENU_RECOMMENT_WRITE = 0; // 덧글 달기
	private final int CONTEXT_MENU_RECOMMEND = 1;		// 추천 하기
	private final int CONTEXT_MENU_MEMO = 2;			// 쪽지 쓰기
	private final int CONTEXT_MENU_ADD_FILTER = 3;		// 필터 추가
	private final int CONTEXT_MENU_DEL_FILTER = 4;		// 필터 제거
	private final int CONTEXT_MENU_DELETE = 5;			// 댓글 삭제

    private final int CONTEXT_MENU_DOWNLOAD = 10;		// 이미지 다운로드
    private final int CONTEXT_MENU_WALLPAPER = 11;		// 배경으로 설정
    
    private final int CONTEXT_MENU_COPY_URL = 12;		// URL 복사
    private final int CONTEXT_MENU_BROWSER = 13;		// 브라우저로 보기
    private final int CONTEXT_MENU_SHARE_LINK = 14;		// 링크 공유
    
    private static final int MENU_CONTENT_REFRESH	= 1; // 새로고침
    private static final int MENU_CONTENT_SETTING	= 2; // 설정

	public ArticleContentActivity() {
		super(R.layout.article_content_layout);
	}

    @Override
    protected void onCreate(Bundle icicle)
    {
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        super.onCreate(icicle);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			articleId = extras.getString(IntentKeys.ARTICLE_ID);
			scrapType = extras.getString(IntentKeys.SCRAP_TYPE);
		}
		
        // 상단의 제목 설정
        setTitle(CurrentInfo.BBS_TITLE);
        
		activity = this;
        dba = DBAdapter.getInstance();
        prefs = PrefUtil.getInstance();
        mHandler = new Handler();
        mIMManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    	// 기존의 데이터를 모두 삭제
    	DBAdapter.getInstance().deleteContentRetrieve();
    	DBAdapter.getInstance().deleteCommentRetrieve();
    	
    	listView = (ListView) findViewById(R.id.article_comment_listView);
    	listView.setScrollbarFadingEnabled(true);
    	listView.setFastScrollEnabled(true);
    	listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    	listView.setOnCreateContextMenuListener(this);
		
        RelativeLayout mListHeader = new RelativeLayout(this);
        listView.addHeaderView(mListHeader);
		LayoutInflater inflater = LayoutInflater.from(this);
		View tv = inflater.inflate(R.layout.article_content_header, null);
		mListHeader.addView(tv, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		listView.addFooterView(inflater.inflate(R.layout.article_content_footer, null));

		// 각 위젯별 이벤트 설정
		initWidgetId();
    	
    	t = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		
		loading = true;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter); 
    	
    	if (articleId == null) {
    		t.setText(R.string.not_exist_article_id);
    		t.show();
    		finish();
    	}
    	
		// 이미 로딩이 된경우 로딩하지 않음.
		if (loading) {
			load();
		}
    }

	@Override
	protected void onPause()
	{
		super.onPause();

		unregisterReceiver(completeReceiver);
		
		ArticleManager.getInstance().removeConsumer();
        callHiddenWebViewMethod("onPause");
//		wContent.loadUrl("about:blank");
	}

	@Override
    protected void onDestroy() {
	    // Because we pass the adapter to the next activity, we need to make
	    // sure it doesn't keep a reference to this activity. We can do this
	    // by clearing its DatasetObservers, which setListAdapter(null) does.
        listView.setAdapter(null);
        adapter = null;
        
        if (adView1 != null) {
            adView1.destroy();
        }
        if (adView2 != null) {
            adView2.destroy();
        }
        
        super.onDestroy();
    }
    
	@Override
	public final void invalidate() {
		super.invalidate();
		
		ArticleManager.getInstance().removeConsumer();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCode.REQ_CODE_ARTICLE_MODIFY
				&& resultCode == Activity.RESULT_OK) {
			// 오류 발생으로 딜레이 후 새로고침
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					onContent();
				}
			}, 300);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
    }

	@Override
	public final void load() {
		super.load();
		super.setRetry(true, MessageHelper.RETRY_GET);
		
		if (scrapType == null) 
			setDefaultData();
		else
			setScrapData();
		
		if (StringUtil.isNotEmpty(URL)) { 
			onContent();
		}
	}

	@Override
	public final void handleEvent(final DataEvent event) {
		if (MessageHelper.DEBUG)
			Log.d("DP", this.getLocalClassName()+".handleEvent(event):"+event.getType());
		
		if (event.getData() != null) {
			switch (event.getType()) {
				case DataEvent.LOGIN:
					if ((Integer)event.getData() == Const.OK) {
						if (StringUtil.equals(AUTH_CHECK, FROM_RCMD))
							onRecommend();
						else if (StringUtil.equals(AUTH_CHECK, FROM_WCMT))
							commentWrite();
						else if (StringUtil.equals(AUTH_CHECK, FROM_CCMT))
							childCommentWrite();
						else if (StringUtil.equals(AUTH_CHECK, FROM_CRMD))
							onCmtRecommend();
						else if (StringUtil.equals(AUTH_CHECK, FROM_CDEL))
							onCmtDelete();
						else if (StringUtil.equals(AUTH_CHECK, FROM_ADEL))
							onContentDelete();
						else if (StringUtil.equals(AUTH_CHECK, FROM_MYDP))
							onSaveMyDp();
					} else {
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
					else {
						if (StringUtil.equals(AUTH_CHECK, FROM_RCMD))
							onRecommend();
						else if (StringUtil.equals(AUTH_CHECK, FROM_WCMT))
							commentWrite();
						else if (StringUtil.equals(AUTH_CHECK, FROM_CCMT))
							childCommentWrite();
						else if (StringUtil.equals(AUTH_CHECK, FROM_CRMD))
							onCmtRecommend();
						else if (StringUtil.equals(AUTH_CHECK, FROM_CDEL))
							onCmtDelete();
						else if (StringUtil.equals(AUTH_CHECK, FROM_ADEL))
							onContentDelete();
						else if (StringUtil.equals(AUTH_CHECK, FROM_MYDP))
							onSaveMyDp();
					}
					break;
				case DataEvent.ARTICLE_CONTENT:
					super.setRetry(false, MessageHelper.RETRY_INIT);
					// 본문 
					setContentWebView();
					// 댓글
					setCommentListView();
					// 댓글 쓰기 후 일경우 젤 밑으로 이동
					if (isCmtRefresh) {
						mHandler.postDelayed(mBottomFocusRunnable, 100);
						isCmtRefresh = false;
					}
					else if (isChdRefresh || isScrapCmmt) {
						mHandler.postDelayed(mPositionFocusRunnable, 100);
						isChdRefresh = false;
						isScrapCmmt = false;
					}
					// 쪽지 확인 요청
					if (prefs.getBoolean(PreferenceKeys.ALERT_MEMO_ENABLED, false)) {
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								MemoManager.getInstance().check();
							}
						}, 300);
					}
					loading = false;
					hideLoadingIndicator(true);
					break;
				case DataEvent.ARTICLE_RECOMMEND:
					switch ((Integer)event.getData()) {
						case Const.OK:
							t.setText(R.string.bbs_toast_recommend_success);
							t.show();
							break;
						case Const.ALREADY_REQUEST:
							t.setText(R.string.bbs_toast_recommend_already);
							t.show();
							break;
						case Const.INTERNAL_SERVER_ERROR:
							t.setText(R.string.bbs_toast_recommend_fail);
							t.show();
							break;
					}
					loading = false;
					hideLoadingIndicator(true);
					break;
				case DataEvent.COMMENT_WRITE:
					if ((Boolean)event.getData()) {
						// 댓글쓰기 성공 시
						findViewById(R.id.article_comment_edit_layout).setVisibility(View.GONE); // 댓글쓰기 창 숨김
						// 버튼 텍스트 변경
						Button addComment = (Button) findViewById(R.id.article_content_comment_button);
						addComment.setText(R.string.button_add_comment);
						// 키보드 숨김
						listView.requestFocus();
						closeSoftKeyboard();
						// 댓글 새로 로드
						onContent();
						// 댓글쓰기후 새로 로드인지 변수 설정
						isCmtRefresh = true;
					} else {
						// 실패 토스트 알림
						t.setText(R.string.bbs_toast_fail_message);
						t.show();
						loading = false;
						hideLoadingIndicator(true);
					}
					break;
				case DataEvent.CHILD_CMT_WRITE:
					if ((Boolean)event.getData()) {
						if (childDialog != null) {
							childDialog.dismiss();
							childDialog = null;
						}
						// 댓글 새로 로드
						onContent();
						// 덧글쓰기후 새로 로드인지 변수 설정
						isChdRefresh = true;
					} else {
						// 실패 토스트 알림
						t.setText(R.string.bbs_toast_fail_message);
						t.show();
						loading = false;
						hideLoadingIndicator(true);
					}
					break;
				case DataEvent.COMMENT_RECOMMEND:
					if ((Boolean)event.getData()) {
						t.setText(R.string.bbs_toast_recommend_success);
						t.show();
					} else {
						t.setText(R.string.bbs_toast_recommend_fail);
						t.show();
					}
					loading = false;
					hideLoadingIndicator(true);
					break;
				case DataEvent.COMMENT_DELETE:
					if ((Boolean)event.getData()) {
						// 삭제 성공 토스트 알림
						t.setText(R.string.deleted);
						t.show();
						// 댓글 새로 로드
						onContent();
					} else {
						// 삭제 실패 토스트 알림
						t.setText(R.string.bbs_toast_fail_message);
						t.show();
						loading = false;
						hideLoadingIndicator(true);
					}
					break;
				case DataEvent.ARTICLE_DELETE:
					if ((Boolean)event.getData()) {
						// 삭제 성공 토스트 알림
						t.setText(R.string.deleted);
						t.show();
						// 삭제 후 종료
						setResult(RESULT_OK);
						finish();
					} else {
						// 삭제 실패 토스트 알림
						t.setText(R.string.bbs_toast_fail_message);
						t.show();
						loading = false;
						hideLoadingIndicator(true);
					}
					break;
				case DataEvent.ARTICLE_SAVE_MYDP:
					if ((Boolean)event.getData()) {
						// 저장 성공 토스트 알림
						t.setText(R.string.article_save_my_dp_success);
						t.show();
					} else {
						// 저장 실패 토스트 알림
						t.setText(R.string.article_save_my_dp_failed);
						t.show();
					}
					loading = false;
					hideLoadingIndicator(true);
					break;
				case DataEvent.SHORTLY_URL:
					if (MessageHelper.DEBUG)
						Log.d("DP", "SHORTLY URL: " + (String)event.getData());
					String url = (String)event.getData();
					if (StringUtil.equals(fromShare, "FB")) {
						url = Const.FACEBOOK_SHARER_URL + url;
					} 
					else if (StringUtil.equals(fromShare, "TW")){
						try {
							Object[] param = new String[2];
							param[0] = StringUtil.trim(url);
							param[1] = URLEncoder.encode(TITLE, encoding);
							url = String.format(Const.TWITTER_SHARER_URL, param);
						} catch (UnsupportedEncodingException e) {}
					}
					loading = false;
					hideLoadingIndicator(true);
					
					if (url != null) {
						if (MessageHelper.DEBUG)
							Log.d("DP", "share url: " + url);
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(intent);
					}
					break;
				default:
					loading = false;
					hideLoadingIndicator(true);
					break;
			}
		} else {
			loading = false;
			hideLoadingIndicator(true);
			super.handleEvent(event);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		if (v.getId() == R.id.article_content_webView) {
			final HitTestResult result = webView.getHitTestResult();
			
		    if (result.getType() == HitTestResult.IMAGE_TYPE ||
		            result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

				urlToDownload = Uri.parse(result.getExtra());
				fileName = StringUtil.substringAfterLast(result.getExtra(), "/");
				filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+File.separator+fileName; 
		    	
		    	menu.setHeaderTitle(result.getExtra());
		        menu.add(Menu.NONE, CONTEXT_MENU_DOWNLOAD, Menu.NONE, R.string.article_context_save_image);
		    	menu.add(Menu.NONE, CONTEXT_MENU_BROWSER, Menu.NONE+1, "브라우저로 열기");
//		        menu.add(Menu.NONE, CONTEXT_MENU_WALLPAPER, Menu.NONE+1, R.string.article_context_set_wallpaper);
		    }
		    else if (result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
				urlToDownload = Uri.parse(result.getExtra());
		    	menu.setHeaderTitle(result.getExtra());
		    	menu.add(Menu.NONE, CONTEXT_MENU_COPY_URL, Menu.NONE, "URL 복사");
		    	menu.add(Menu.NONE, CONTEXT_MENU_BROWSER, Menu.NONE+1, "브라우저로 열기");
		    	menu.add(Menu.NONE, CONTEXT_MENU_SHARE_LINK, Menu.NONE+2, "링크 공유");
		    }
		}
		else 
		{
	    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    	if (info.position > 0) {
	    		sComment = (Comment) adapter.getItem(info.position-1);
	    		sComment.setPosition(info.position);
				
				if (sComment == null)
					return;
				
				if (StringUtil.isNotEmpty(sComment.getAvatarUrl())) { // 댓글인 경우
					menu.add(Menu.NONE, CONTEXT_MENU_RECOMMENT_WRITE, Menu.NONE, R.string.comment_detail_add_comment).setEnabled(true);
					menu.add(Menu.NONE, CONTEXT_MENU_RECOMMEND, Menu.NONE+1, R.string.comment_detail_do_recommend).setEnabled(true);
				}
				
				menu.add(Menu.NONE, CONTEXT_MENU_MEMO, Menu.NONE+2, R.string.comment_detail_do_memo).setEnabled(true);
	
				if (StringUtil.equals(prefs.getString(PreferenceKeys.ACCOUNT_ID, null), 
						sComment.getUserId()))
					menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE+3, R.string.comment_detail_do_delete).setEnabled(true);
				else {
					String[] fIdList = StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR);
					if (fIdList != null && StringUtil.contains(fIdList, sComment.getUserId()))
						menu.add(Menu.NONE, CONTEXT_MENU_DEL_FILTER, Menu.NONE+3, R.string.article_context_del_filter).setEnabled(true);
					else
						menu.add(Menu.NONE, CONTEXT_MENU_ADD_FILTER, Menu.NONE+3, R.string.article_context_add_filter).setEnabled(true);
				}
	    	}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case CONTEXT_MENU_RECOMMENT_WRITE:
				if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) {
					// 계정 정보 없이 덧플 쓸려고 요청할시 메시지로 알림 
					DialogBuilder.createAlertDialog(activity, 
							getString(R.string.dialog_comment_alert_message))
							.show();

				} else {
					listView.setSelection(sComment.getPosition());
					
					LayoutInflater factory = LayoutInflater.from(this);
		            final View textView = factory.inflate(R.layout.article_comment_dialog_layout, null);
		            TextView dialogText = (TextView) textView.findViewById(R.id.article_comment_dialog_upper_textView);
		            dialogText.setText(sComment.getUserName()+":"+DpUtil.deleteTagBR(sComment.getContent()));
		            final EditText dialogEdit = (EditText) textView.findViewById(R.id.article_comment_dialog_content_editText);
		            
					DialogBuilder.createConfirmDialog(activity, 
							getString(R.string.comment_detail_add_comment), 
							textView, 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(final DialogInterface dialog, final int which) {
									String cText = dialogEdit.getText().toString();
									if (StringUtil.isNotEmpty(StringUtil.trimToNull(cText))) {
				                    	//dialog.dismiss();
										childDialog = dialog;
			
										// 키보드 숨김
										listView.requestFocus();
										mIMManager.hideSoftInputFromWindow(dialogEdit.getWindowToken(), 0);
										
										// 자동저장 활성화시 클립보드에 저장
										if (prefs.getBoolean(PreferenceKeys.SAVE_COMMENT_ENABLED, true))
											SystemUtil.copyToClipboard(activity, cText);
										
										// 덧글 전송 요청
										COMMENT = StringUtil.newLineToBr(cText);
										onChildComment();
									}
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(final DialogInterface dialog, final int which) {
									// 키보드 숨김
									listView.requestFocus();
									mIMManager.hideSoftInputFromWindow(dialogEdit.getWindowToken(), 0);
									// 다이얼로그 닫기
									dialog.dismiss();
								}
							}
					).show();
				}
				break;
			case CONTEXT_MENU_RECOMMEND:
				if (DpUtil.isLogined()) { // 로그인 상태 일 경우 바로 추천 실행
					onCmtRecommend();
				}
				else if (DpUtil.isAutoLoginEnabled()) { // 로그인 상태가 아니고 자동로그인 활성화 상태일 경우 로그인 실행
					AUTH_CHECK = FROM_CRMD;
					onLoginCheck();
				}
				else if (StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보만 있을 경우 물어봄
					DialogBuilder.createConfirmDialog(activity, 
											getString(R.string.dialog_login_check_message), 
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(final DialogInterface dialog, final int which) {
							                    	dialog.dismiss();
							    					AUTH_CHECK = FROM_CRMD;
							                    	onLoginCheck();
												}
											}).show();
				}
				else { // 계정 정보가 없을 경우 추천할 수 없음을 알려줌.
					DialogBuilder.createAlertDialog(activity, 
							getString(R.string.dialog_recommend_alert_message))
							.show();
				}
				break;
			case CONTEXT_MENU_MEMO:
				Intent intent = new Intent(ArticleContentActivity.this, MemoWriteActivity.class);
				intent.putExtra(IntentKeys.MEMO_RECEIVER, sComment.getUserId());
				startActivity(intent);
				break;
			case CONTEXT_MENU_ADD_FILTER:
				if (prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null) == null) {
					prefs.setString(PreferenceKeys.FILTERING_ID_LIST, sComment.getUserId());
					prefs.setString(PreferenceKeys.FILTERING_NICK_LIST, sComment.getUserName());
				} else { 
					prefs.setString(PreferenceKeys.FILTERING_ID_LIST, 
							prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null) + Const.DEFAULT_SEPARATOR + sComment.getUserId());
					prefs.setString(PreferenceKeys.FILTERING_NICK_LIST, 
							prefs.getString(PreferenceKeys.FILTERING_NICK_LIST, null) + Const.DEFAULT_SEPARATOR + sComment.getUserName());
				}
				Toast.makeText(activity, getText(R.string.msg_filtering_added), Toast.LENGTH_SHORT).show();
				break;
			case CONTEXT_MENU_DEL_FILTER:
				String[] idList = StringUtil.remove(
										StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_ID_LIST, null), Const.DEFAULT_SEPARATOR),
										sComment.getUserId());
				String[] nickList = StringUtil.remove(
										StringUtil.split(prefs.getString(PreferenceKeys.FILTERING_NICK_LIST, null), Const.DEFAULT_SEPARATOR),
										sComment.getUserName());
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
		        						StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보만 있을 경우 물어봄
		        					DialogBuilder.createConfirmDialog(activity, 
		        											getString(R.string.dialog_login_check_message), 
		        											new DialogInterface.OnClickListener() {
		        												@Override
		        												public void onClick(final DialogInterface dialog, final int which) {
		        							                    	dialog.dismiss();
		        							    					AUTH_CHECK = FROM_CDEL;
		        							                    	onLoginCheck();
		        												}
		        											}).show();
		        				}
		        				else if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보가 없을 경우 추천할 수 없음을 알려줌.
		        					DialogBuilder.createAlertDialog(activity, 
		        							getString(R.string.dialog_recommend_alert_message))
		        							.show();
		        				}
		        				else {
		        					AUTH_CHECK = FROM_CDEL;
		        					onLoginCheck();
		        				}
							}
				}).show();
				break;
        	case CONTEXT_MENU_DOWNLOAD:
	    		List<String> pathSegments = urlToDownload.getPathSegments();
	    	    DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
	    	    DownloadManager.Request request = new DownloadManager.Request(urlToDownload);
	    		request.setTitle("DVD PRIME");
	    		request.setDescription("Downloader");
	    		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
	    		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size()-1));
	    		File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	    		if (!f.exists())
	    			f.mkdirs();
	    		
	    		downloadManager.enqueue(request);
	    		
	    		break;
        	case CONTEXT_MENU_WALLPAPER:
        		intent = new Intent(Intent.ACTION_ATTACH_DATA);
        		intent.setDataAndType(urlToDownload, "image/jpg");
        		intent.putExtra("mimeType", "image/jpg");
        		startActivity(intent);
        		break;
        	case CONTEXT_MENU_COPY_URL:
        		SystemUtil.copyToClipboard(activity, urlToDownload.toString());
        		break;
        	case CONTEXT_MENU_BROWSER:
        		Intent i = new Intent(Intent.ACTION_VIEW);
        		i.setData(urlToDownload); 
        		startActivity(i);
        		break;
        	case CONTEXT_MENU_SHARE_LINK:
        		Intent it = new Intent(android.content.Intent.ACTION_SEND);
                it.setType("text/plain");
                it.putExtra(Intent.EXTRA_SUBJECT, TITLE);
                it.putExtra(Intent.EXTRA_TEXT, urlToDownload.toString());
                startActivity(Intent.createChooser(it, "공유하기"));
        		break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_CONTENT_REFRESH, Menu.NONE, R.string.menu_refresh).setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, MENU_CONTENT_SETTING, Menu.NONE, R.string.menu_setting).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		Intent intent;
		switch (item.getItemId()) {
			case MENU_CONTENT_REFRESH:
				loading = true;
				load();
				break;
			case MENU_CONTENT_SETTING:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		
		return false;
	}

	/**
	 * 댓글 내용 전송 
	 */
	public void commentWrite() {
		CommentManager cm = CommentManager.getInstance();
		cm.addConsumer(this, DataEvent.COMMENT_WRITE);
		cm.write(BBS_ID, COMMENT);
		showLoadingIndicator(R.string.bbs_progressbar_save);
	}
	
	private BroadcastReceiver completeReceiver = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, R.string.msg_download_manager_complete, Toast.LENGTH_SHORT).show();
			// Tell the media scanner about the new file so that it is
	        // immediately available to the user.
			new SystemUtil.MediaScannerNotifier(activity, filePath, "image/*");
		}
	};
	
	/**
	 * 덧글 내용 전송
	 */
	private void childCommentWrite() {
		CommentManager cm = CommentManager.getInstance();
		cm.addConsumer(this, DataEvent.CHILD_CMT_WRITE);
		cm.childWrite(BBS_ID, sComment.getCommentId(), COMMENT);
		showLoadingIndicator(R.string.bbs_progressbar_save);
	}
	
	private void initWidgetId() {
		// 웹뷰 설정
    	webView = (WebView) findViewById(R.id.article_content_webView);
    	if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) { // Gingerbread 이상
    		registerForContextMenu(webView);
    	}
    	// 컨텐츠 텍스트 뷰 설정
    	contentTextView = (TextView) findViewById(R.id.article_message_textView);
    	// 복사 버튼 설정
    	Button btnCopy = (Button)findViewById(R.id.article_content_top_button_copy_btn);
    	btnCopy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SystemUtil.copyToClipboard(getBaseContext(), URL);
			}
    	});
    	// 추천 버튼 설정
    	Button btnRcmd = (Button)findViewById(R.id.article_content_top_button_recommend_btn);
    	btnRcmd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!DpUtil.isAutoLoginEnabled() &&
						StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보만 있을 경우 물어봄
					DialogBuilder.createConfirmDialog(activity, 
											getString(R.string.dialog_login_check_message), 
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(final DialogInterface dialog, final int which) {
							                    	dialog.dismiss();
							    					AUTH_CHECK = FROM_RCMD;
							                    	onLoginCheck();
												}
											}).show();
				}
				else if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보가 없을 경우 추천할 수 없음을 알려줌.
					DialogBuilder.createAlertDialog(activity, 
							getString(R.string.dialog_recommend_alert_message))
							.show();
				}
				else {
					AUTH_CHECK = FROM_RCMD;
					onLoginCheck();
				}
			}
    	});
    	// 수정 보내기 버튼 설정
    	Button btnModify = (Button)findViewById(R.id.article_content_top_button_modify_btn);
    	btnModify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArticleContentActivity.this, ArticleWriteActivity.class);
				intent.putExtra(IntentKeys.WRITE_MODE, "1");
				intent.putExtra(IntentKeys.BBS_ID, BBS_ID);
				intent.putExtra(IntentKeys.BBS_DATE, REG_DATE);
				startActivityForResult(intent, RequestCode.REQ_CODE_ARTICLE_MODIFY);
			}
    	});
    	// 삭제 보내기 버튼 설정
    	Button btnDelete = (Button)findViewById(R.id.article_content_top_button_delete_btn);
    	btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogBuilder.createConfirmDialog(activity, 
						getString(R.string.dialog_content_delete_message), 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, final int which) {
		                    	dialog.dismiss();
		        				AUTH_CHECK = FROM_ADEL;
		                    	onLoginCheck();
							}
						}).show();
			}
    	});
    	// 쪽지 보내기 버튼 설정
    	Button btnMemo = (Button)findViewById(R.id.article_content_top_button_memo_btn);
    	btnMemo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ArticleContentActivity.this, MemoWriteActivity.class);
				intent.putExtra(IntentKeys.MEMO_RECEIVER, USER_ID);
				startActivity(intent);
			}
    	});
    	// MY 디피에 저장 버튼 설정
    	Button btnSave = (Button)findViewById(R.id.article_content_top_button_my_dp_btn);
    	btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!DpUtil.isAutoLoginEnabled() &&
						StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보만 있을 경우 물어봄
					DialogBuilder.createConfirmDialog(activity, 
											getString(R.string.dialog_login_check_message), 
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(final DialogInterface dialog, final int which) {
							                    	dialog.dismiss();
							    					AUTH_CHECK = FROM_MYDP;
							                    	onLoginCheck();
												}
											}).show();
				}
				else if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보가 없을 경우 실행할 수 없음을 알려줌.
					DialogBuilder.createAlertDialog(activity, 
							getString(R.string.dialog_my_dp_save_alert_message))
							.show();
				}
				else {
					AUTH_CHECK = FROM_MYDP;
					onLoginCheck();
				}
			}
    	});
    	// 박스웹 보기
		Button viewBoxweb = (Button)findViewById(R.id.article_content_top_button_boxweb_btn);
		viewBoxweb.setVisibility(View.GONE);
		viewBoxweb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				String url = StringUtil.replace(URL, Const.CONTENT_URL, Const.BOXWEB_URL);
				url = StringUtil.replace(url, "/view.asp", "");
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		});
		// 홈페이지 원문 보기
		Button viewHomepage = (Button) findViewById(R.id.article_content_top_button_original_btn);
		viewHomepage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
				startActivity(intent);
			}
		});
		// 댓글 쓰기 버튼
		final Button addComment = (Button) findViewById(R.id.article_content_comment_button);
		addComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보가 없을 경우
					DialogBuilder.createConfirmDialog(activity, 
											getString(R.string.dialog_require_account_message), 
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(final DialogInterface dialog, final int which) {
							                    	dialog.dismiss();
							        				Intent intent = new Intent(ArticleContentActivity.this, AccountSettingActivity.class);
							        				startActivity(intent);
												}
											}).show();
				} else {
					if (StringUtil.equals(addComment.getText().toString(), getString(R.string.button_add_comment))) {
						addComment.setText(R.string.button_close_comment);
						
						LinearLayout addCommentLayout = (LinearLayout)findViewById(R.id.article_comment_edit_layout);
						addCommentLayout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_in));
						addCommentLayout.setVisibility(View.VISIBLE);
					} else {
						addComment.setText(R.string.button_add_comment);
						
						closeSoftKeyboard();

						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								LinearLayout addCommentLayout = (LinearLayout)findViewById(R.id.article_comment_edit_layout);
								addCommentLayout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_out));
								addCommentLayout.setVisibility(View.GONE);
							}
						}, 300);
					}
				}
			}
		});
		// 댓글 입력 창 설정
    	final EditText commentEditText = (EditText) findViewById(R.id.article_comment_editText);
    	commentEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND){
					checkMessage(commentEditText);
				}
				return false;
			}
		});
		// 페이스북 공유 버튼
		Button shareFacebook = (Button) findViewById(R.id.article_content_top_button_share_fb_btn);
		shareFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				fromShare = "FB";
				onShortlyUrl();
			}
		});
		// 트위터 공유 버튼
		Button shareTwitter = (Button) findViewById(R.id.article_content_top_button_share_twitter_btn);
		shareTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				fromShare = "TW";
				onShortlyUrl();
			}
		});
		commentEditText.setOnFocusChangeListener(onFocusChangeListener);
		commentEditText.setPrivateImeOptions("defaultInputmode=korea;"); // 기본 한글 키패드로 설정
		commentEditText.addTextChangedListener(mCmtTextWatcher);
    	// 댓글 전송 버튼 설정
		mSendButton = (Button) findViewById(R.id.article_comment_send_btn);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkMessage(commentEditText);
			}
		});
		SendButtonSetEnabled(false);
		
		
		/////////////////////////////////////////////////////////////////////////////////////
		// 광고 설정
		/////////////////////////////////////////////////////////////////////////////////////
		setAdam();
	}
	
	private Runnable mFocusRunnable = new Runnable()
	{
		public void run()
		{
			listView.setSelection(adapter.getCount()-1);
		}
	};

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean f) {
			if (f && adapter != null && adapter.getCount() > 0) {
				mHandler.postDelayed(mFocusRunnable, 600);
			}
		}
		
	};
	
	private void setContentWebView() {
		Cursor c = DpUtil.query(this, DpDB.Content.CONTENT_URI, null, null, null, null);
		if (c != null && c.moveToFirst()) {
			String content = c.getString(c.getColumnIndexOrThrow(DpDB.Content.CTT_CONTENT));
			
			// 텍스트만 보기 모드인지 여부 확인
			if (prefs.getBoolean(PreferenceKeys.CONTENT_TEXT_ONLY, false)) {
		        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.WHITE_THEME)) {
		        	contentTextView.setTextColor(Color.DKGRAY);
		        	contentTextView.setBackgroundColor(Color.WHITE);
		        }
				content = StringUtil.removeHtmlTags(content);
				contentTextView.setText(content);
				contentTextView.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
			} else {
				contentTextView.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				
				webView.getSettings().setDefaultTextEncodingName(encoding);
				webView.getSettings().setLoadsImagesAutomatically(true);
				webView.getSettings().setJavaScriptEnabled(true);
				
				// 설정에서 플러그인 체크시에만 활성화 
//				webView.getSettings().setPluginsEnabled(prefs.getBoolean(PreferenceKeys.WEBVIEW_PLUGIN_ENABLED, false));
				if (prefs.getBoolean(PreferenceKeys.WEBVIEW_PLUGIN_ENABLED, false)) {
					webView.getSettings().setPluginState(PluginState.ON);
				} else {
					webView.getSettings().setPluginState(PluginState.ON_DEMAND);
				}
//				wContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//				wContent.getSettings().setBuiltInZoomControls(true);
//				wContent.getSettings().setSupportZoom(true);
//				wContent.getSettings().setUseWideViewPort(true);
				webView.getSettings().setLoadWithOverviewMode(true);
				webView.getSettings().setDefaultTextEncodingName(encoding);
				webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
				webView.getSettings().setMinimumFontSize(12);
				webView.getSettings().setDefaultFontSize(StringUtil.toNumber(prefs.getString(PreferenceKeys.CONTENT_FONTSIZE, "26")));
				webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//				wContent.getSettings().setDefaultFixedFontSize(30);
				webView.setWebChromeClient(new WebChromeClient() {
					   public void onProgressChanged(WebView view, int progress) {
					       activity.setProgress(progress * 100);
					   }
				});
				webView.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String overrideUrl) {
						if(overrideUrl.startsWith("http")) {
							Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(overrideUrl));
			        		startActivity(i);
							return true;				
						} else {
				        	boolean override = false;
			            	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(overrideUrl));
			            	intent.addCategory(Intent.CATEGORY_BROWSABLE);
			            	intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
			            	try {
			                	startActivity(intent);
			                	override = true;
			                } catch (ActivityNotFoundException ex) {
			                }
			            	return override;
						}		
					}
				});
				
				if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
					webView.loadData(Uri.encode(content), mimeType, encoding);
				else 
					webView.loadDataWithBaseURL(null, content, null, encoding, null);
			}
    		// 제목 설정
    		TextView aTitle = (TextView)findViewById(R.id.article_content_subject_textView);
    		aTitle.setText(StringUtil.decode(c.getString(c.getColumnIndexOrThrow(DpDB.Content.CTT_TITLE))));
    		TITLE = aTitle.getText().toString();
    		// 아바타 설정
			String avatarUrl = c.getString(c.getColumnIndexOrThrow(DpDB.Content.CTT_URL));
			avatarUrl = StringUtil.isNotEmpty(avatarUrl) ? Const.HOMEPAGE_URL + avatarUrl : null;
			if (avatarUrl != null) {
				ImageView aImage = (ImageView)findViewById(R.id.article_content_avatar_imageView);
				WebImageView wiv = new WebImageView(aImage);
				ImageManager.getInstance().setImage(avatarUrl, wiv);
			} else {
				t.setText(R.string.msg_drop_out_user_content);
				t.show();
			}
			// 태그 설정
			String tag = c.getString(c.getColumnIndexOrThrow(DpDB.Content.CTT_TAG));
			if (StringUtil.isNotEmpty(tag)) {
				TextView aTag = (TextView)findViewById(R.id.article_content_tag_textView);
				// 테마에 맞게 설정 변경
		        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.WHITE_THEME)) {
		        	aTag.setBackgroundColor(Color.WHITE);
		        }
		        // 값 설정		        	
				aTag.setText(String.format(getString(R.string.content_tag), tag));
				aTag.setVisibility(View.VISIBLE);
			}
		}
		if (c != null)
			c.close(); // Cursor Close
	}
	
	private void setCommentListView() {
		if (dba.getCommentList() != null) {
			if (adapter == null ||
					(StringUtil.equals(AUTH_CHECK, FROM_WCMT) && dba.getCommentList().size() == 1)) {
				adapter = new CommentListAdapter(this,
									R.layout.article_comment_list_row, 
									dba.getCommentList());
				
				// 댓글창고에서 전달된 경우 해당 댓글을 설정해준다.
				if (StringUtil.equals(scrapType, Const.TYPE_CMT)
						&& StringUtil.isNotEmpty(commentId)) {
					isScrapCmmt = true;
					sComment = new Comment();
					sComment.setPosition(dba.getCommentBelowCount(commentId));
				}
				
				listView.setAdapter(adapter);
			} else {
				@SuppressWarnings("unchecked")
				List<Comment> items = (List<Comment>) adapter.getItems();
				items.clear();
				items.addAll(dba.getCommentList());
				
				adapter.notifyDataSetChanged();
			}

			// 댓글 목록이 있을 경우 제일 하단으로 가는 이벤트를 활성화 시킨다.
			RelativeLayout downArrowLayout = (RelativeLayout)findViewById(R.id.article_content_arrow_down_layout);
			downArrowLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listView.setSelection(adapter.getCount());
				}
			});
			// 댓글 목록이 있을 경우 제일 상단으로 가는 이벤트를 활성화 시킨다.
			RelativeLayout upArrowLayout = (RelativeLayout)findViewById(R.id.article_content_arrow_up_layout);
			upArrowLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listView.setSelection(0);
				}
			});
		}
		
	}
	
	/**
	 * 아담 광고 설정하기
	 */
	private void setAdam() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.article_content_footer_layout);
		if (layout.findViewById(1) == null) {
			// Ad@m sdk 초기화 시작
			adView1 = new AdView(this);
			// 할당 받은 clientId 설정
			adView1.setClientId("a31Z0DT130bd137a96");
	        adView1.setId(1);
			adView1.setOnAdFailedListener(new OnAdFailedListener() {
				@Override
				public void OnAdFailed(AdError arg0, String arg1) {
					if (adView1 != null) {
						adView1.pause();
						adView1.setVisibility(View.GONE);
					}
//					setCauly();
					setAdMob();
				}
			});
			adView1.setOnAdLoadedListener(new OnAdLoadedListener() {
				@Override
				public void OnAdLoaded() {
					if (adView1 != null)
						adView1.setVisibility(View.VISIBLE);
					if (adView2 != null)
						adView2.setVisibility(View.GONE);
				}
			});
			// 광고 갱싞 시간 : 기본 60초
			adView1.setRequestInterval(12);
			// Animation 효과 : 기본 값은 AnimationType.NONE
			adView1.setAnimationType(AnimationType.FLIP_HORIZONTAL);
			//레이아웃 적용.
			layout.addView(adView1);
		}
	}
	
	/**
	 * AdMob 광고 설정하기 
	 */
	private void setAdMob() {
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.article_content_footer_layout);
		
		if (layout.findViewById(2) == null) {
			// Create the adView
			adView2 = new com.google.ads.AdView(this, AdSize.SMART_BANNER, "a14df6dec08f8ce");
			adView2.setId(2);
			adView2.setVisibility(View.VISIBLE);
			// Add the adView to it
		    layout.addView(adView2);
		    // Initiate a generic request to load it with an ad
		    adView2.loadAd(new AdRequest());
		}
	}
	
	/**
	 * 카울리 광고 설정하기
	 */
//	private void setCauly() {
//		LinearLayout layout = (LinearLayout)findViewById(R.id.article_content_footer_layout);
//		if (layout.findViewById(2) == null) {
//	        //데이터 채우기
//	        CaulyAdInfo info = new CaulyAdInfoBuilder("a47UTACO4").
//	        	effect("BottomSlide").
//	        	bannerHeight("Proportional").
//	        	build();
//	        //광고 View
//	        adView2 = new CaulyAdView(this);
//	        adView2.setId(2);
//	        adView2.setAdInfo(info);
//	        adView2.setVisibility(View.VISIBLE);
//			//레이아웃 적용.
//			layout.addView(adView2);
//		}
//	}
	
	/**
	 * 키보드 숨기기 
	 */
	private void closeSoftKeyboard() {
		EditText editText = (EditText) findViewById(R.id.article_comment_editText);
		mIMManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
	 * 본문 새로 고침
	 */
	private void onContent() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_CONTENT);
			am.get(URL);
			showLoadingIndicator(R.string.bbs_progressbar_loading);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 본문 추천
	 */
	private void onRecommend() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_RECOMMEND);
			am.recommend(BBS_ID);
			showLoadingIndicator(R.string.bbs_progressbar_recommending);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}

	/**
	 * 댓글 추천
	 */
	private void onCmtRecommend() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			CommentManager cm = CommentManager.getInstance();
			cm.addConsumer(this, DataEvent.COMMENT_RECOMMEND);
			cm.recommend(sComment.getCommentId());
			showLoadingIndicator(R.string.bbs_progressbar_recommending);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}

	/**
	 * 댓글 삭제
	 */
	private void onCmtDelete() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			CommentManager cm = CommentManager.getInstance();
			cm.addConsumer(this, DataEvent.COMMENT_DELETE);
			cm.delete(BBS_ID, sComment.getCommentId());
			showLoadingIndicator(R.string.bbs_progressbar_deleting);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}

	/**
	 * 본문 삭제
	 */
	private void onContentDelete() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_DELETE);
			am.delete(CurrentInfo.BBS_MAJOR, CurrentInfo.BBS_MINOR, CurrentInfo.BBS_MASTER_ID, BBS_ID);
			showLoadingIndicator(R.string.bbs_progressbar_deleting);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * MY 디피에 저장
	 */
	private void onSaveMyDp() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_SAVE_MYDP);
			am.saveMyDp(BBS_ID);
			showLoadingIndicator(R.string.bbs_progressbar_save);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 단축 URL 
	 */
	private void onShortlyUrl() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.SHORTLY_URL);
			am.shortlyUrl(URL);
			showLoadingIndicator(R.string.bbs_progressbar_loading);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 기본 정보 설정 (기본)
	 */
	private void setDefaultData() {
		Cursor c = DpUtil.query(this, Uri.withAppendedPath(DpDB.Article.CONTENT_URI, Uri.encode(articleId)), null, null, null, null);
		
    	if (c != null && c.moveToFirst()) {
    		// 제목 설정
    		TextView aTitle = (TextView)findViewById(R.id.article_content_subject_textView);
    		aTitle.setText(StringUtil.decode(c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_TITLE))));
    		// 닉네임 설정
    		TextView aNickname = (TextView)findViewById(R.id.article_content_nickname_textView);
    		aNickname.setText(c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_USER_NAME)));
    		// 등록일 설정
    		TextView aWriteDate = (TextView)findViewById(R.id.article_content_write_date_textView);
    		aWriteDate.setText(c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_DATE)));
    		// 본인 글일 경우에만 버튼 활성화 - 수정/삭제
    		if (StringUtil.equals(prefs.getString(PreferenceKeys.ACCOUNT_ID, null),
    				c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_USER_ID)))) {
    			findViewById(R.id.article_content_top_button_modify_btn).setVisibility(View.VISIBLE);
    			findViewById(R.id.article_content_top_button_delete_btn).setVisibility(View.VISIBLE);
    		}
    		// 게시물의 URL을 설정
    		URL = Const.CONTENT_URL + c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_URL)).substring(1);
    		// 게시물의 ID를 설정
    		BBS_ID = StringUtil.substringBefore(StringUtil.substringAfter(URL, "bbslist_id="), "&page");
    		// 게시물의 작성자 아아디 설정
    		USER_ID = c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_USER_ID));
    		// 게시물의 등록일
    		REG_DATE = c.getString(c.getColumnIndexOrThrow(DpDB.Article.ATC_DATE));
    		// Cursor 닫기
    		c.close();
    	}
	}

	/**
	 * 기본 정보 설정 (스크랩)
	 */
	private void setScrapData() {
		Cursor c = DpUtil.query(this, Uri.withAppendedPath(DpDB.Scrap.CONTENT_URI, Uri.encode(articleId)), null, null, null, null);
		
    	if (c != null && c.moveToFirst()) {
    		// 제목 설정
    		TextView aTitle = (TextView)findViewById(R.id.article_content_subject_textView);
    		aTitle.setText(StringUtil.decode(c.getString(c.getColumnIndexOrThrow(DpDB.Scrap.TITLE))));
    		// 닉네임 설정
    		TextView aNickname = (TextView)findViewById(R.id.article_content_nickname_textView);
    		aNickname.setText(c.getString(c.getColumnIndexOrThrow(DpDB.Scrap.USER_NAME)));
    		// 등록일 설정
    		TextView aWriteDate = (TextView)findViewById(R.id.article_content_write_date_textView);
    		aWriteDate.setText(c.getString(c.getColumnIndexOrThrow(DpDB.Scrap.DATE)));
    		// 게시물의 URL을 설정
    		URL = Const.HOMEPAGE_URL + c.getString(c.getColumnIndexOrThrow(DpDB.Scrap.URL));
    		// 게시물의 ID를 설정
    		BBS_ID = StringUtil.substringAfter(URL, "bbslist_id=");
    		// 게시물의 작성자 아아디 설정
    		USER_ID = null;
    		// 게시물의 등록일
    		REG_DATE = c.getString(c.getColumnIndexOrThrow(DpDB.Scrap.DATE));
    		// 댓글 창고에서 클릭시 해당 댓글로 이동하기 위한 정보
    		if (StringUtil.equals(scrapType, Const.TYPE_CMT))
    			commentId = c.getString(c.getColumnIndexOrThrow(DpDB.Scrap.RCMD));
    		// Cursor 닫기
    		c.close();
    	}
	}

	/**
     * Quick edit text, of message body, watcher for text counter 
     */
	private TextWatcher mCmtTextWatcher = new TextWatcher() {
    	
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			onUserInteraction();
            
        	if(!mSendButton.isEnabled()) {
        		if(s.length() > 0)
        			SendButtonSetEnabled(true);
        	} else {
        		if(s.length() == 0)
        			SendButtonSetEnabled(false);
        	}
		}

		public void afterTextChanged(Editable s) {
		}
	};

    private void SendButtonSetEnabled(boolean value){
    	mSendButton.setEnabled(value);
    	if( value == true ){
    		mSendButton.setShadowLayer(0.3f, -1, -1, Color.parseColor("#80000000"));
    	}else{
    		mSendButton.setShadowLayer(0, 0, 0, Color.parseColor("#80000000"));
    	}    	
    }

	/**
	 * 댓글 전송
	 * @param mText
	 */
	private void checkMessage(EditText mText)
	{
		COMMENT = StringUtil.newLineToBr(mText.getText().toString());
		
		if (StringUtil.trimToNull(COMMENT) != null) {
			if (!DpUtil.isAutoLoginEnabled() &&
					StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보만 있을 경우 물어봄
				DialogBuilder.createConfirmDialog(activity, 
										getString(R.string.dialog_login_check_message), 
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(final DialogInterface dialog, final int which) {
						                    	dialog.dismiss();
						        				AUTH_CHECK = FROM_WCMT;
						                    	onLoginCheck();
											}
										}).show();
			}
			else if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보가 없을 경우 실행할 수 없음을 알려줌.
				DialogBuilder.createAlertDialog(activity, 
						getString(R.string.dialog_comment_alert_message))
						.show();
			}
			else {
				AUTH_CHECK = FROM_WCMT;
				onLoginCheck();
			}
		}
	}
	
	private void onChildComment() {
		if (!DpUtil.isAutoLoginEnabled() &&
				StringUtil.isNotEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보만 있을 경우 물어봄
			DialogBuilder.createConfirmDialog(activity, 
									getString(R.string.dialog_login_check_message), 
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(final DialogInterface dialog, final int which) {
					                    	dialog.dismiss();
					        				AUTH_CHECK = FROM_CCMT;
					                    	onLoginCheck();
										}
									}).show();
		}
		else if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))) { // 계정 정보가 없을 경우 실행할 수 없음을 알려줌.
			DialogBuilder.createAlertDialog(activity, 
					getString(R.string.dialog_comment_alert_message))
					.show();
		}
		else {
			AUTH_CHECK = FROM_CCMT;
			onLoginCheck();
		}
	}
	
	private Runnable mBottomFocusRunnable = new Runnable()
	{
		public void run()
		{
			if (adapter != null && adapter.getCount() > 0)
				listView.setSelection(adapter.getCount());
		}
	};

	private Runnable mPositionFocusRunnable = new Runnable()
	{
		public void run()
		{
			if (adapter != null && adapter.getCount() > 0) {
				if (sComment != null && sComment.getPosition() > 0)
					listView.setSelection(sComment.getPosition());
			}
		}
	};

	private void callHiddenWebViewMethod(String name){
	    if( webView != null ){
	        try {
	            Method method = WebView.class.getMethod(name);
	            method.invoke(webView);
	        } catch (NoSuchMethodException e) {
//	            Log.e(TAG, "No such method: " + name, e);
	        } catch (IllegalAccessException e) {
//	            Log.e(TAG, "Illegal Access: " + name, e);
	        } catch (InvocationTargetException e) {
//	            Log.e(TAG, "Invocation Target Exception: " + name, e);
	        }
	    }
	}

	/* (non-Javadoc)
	 * 카울리 광고 다운로드 실패
	 * @see com.cauly.android.ad.AdListener#onFailedToReceiveAd(boolean)
	 */
//	@Override
//	public void onFailedToReceiveAd(boolean arg0) {
//		if (adView1 != null)
//			adView1.setVisibility(View.GONE);
//		if (adView2 != null)
//			adView2.setVisibility(View.GONE);
//	}

	/* (non-Javadoc)
	 * 카울리 광고 다운로드 성공
	 * @see com.cauly.android.ad.AdListener#onReceiveAd()
	 */
//	@Override
//	public void onReceiveAd() {
//		if (adView1 != null)
//			adView1.setVisibility(View.GONE);
//		if (adView2 != null)
//			adView2.setVisibility(View.VISIBLE);
//	}

//	@Override
//	public void onCloseInterstitialAd() {
//		adView2.stopLoading();
//	}

}
