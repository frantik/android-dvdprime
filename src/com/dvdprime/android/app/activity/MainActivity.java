package com.dvdprime.android.app.activity;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.util.PrefUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 처음 메뉴 목록
 *
 * @author Kwang-myung,Choi (frantik@gmail.com)
 */
public class MainActivity extends BaseActivity implements OnClickListener {
	
	public MainActivity() {
		super(R.layout.main_layout);
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ImageView goBbsTabText = (ImageView)findViewById(R.id.main_dp_logo_imageView);
		goBbsTabText.setOnClickListener(this);
		
		Button goScrapList = (Button)findViewById(R.id.main_button_scrap_Button);
		Button goDocumentList = (Button)findViewById(R.id.main_button_document_Button);
		Button goCommentList = (Button)findViewById(R.id.main_button_comment_Button);
		Button goMemoText = (Button)findViewById(R.id.main_button_memo_imageView);
		Button goSettingsText = (Button)findViewById(R.id.main_button_setting_imageView);
		
		goScrapList.setOnClickListener(this);
		goDocumentList.setOnClickListener(this);
		goCommentList.setOnClickListener(this);
		goMemoText.setOnClickListener(this);
		goSettingsText.setOnClickListener(this);
		
		// 설정에 게시판 목록 바로가기 활성화시
		if (PrefUtil.getInstance().getBoolean(PreferenceKeys.DIRECT_BBS_ENABLED, false)) {
			startActivity(new Intent(MainActivity.this, DpTabActivity.class));
			finish();
		}
    }
    
    @Override
    protected void onDestroy() {
    	PrefUtil.getInstance().setInt(PreferenceKeys.REQUEST_AD_COUNT, 1);
    	super.onDestroy();
    }


    @Override
    public void onBackPressed() {
		DialogInterface.OnClickListener positivListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				finish();
//				System.exit(0);
			}
		};
		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
			}
		};
		if (PrefUtil.getInstance().getBoolean(PreferenceKeys.CLOSE_DIALOG_ENABLED, true)) {
			DialogBuilder.createConfirmDialog(this, 
										getString(R.string.show_close_title),
										getString(R.string.show_warning_close),
										R.string.button_ok,
										R.string.button_cancel,
										positivListener,
										negativeListener).show();
		} else {
			super.onBackPressed();
//			System.exit(0);
		}
    }
    
	@Override
	public void onClick(View v) {
		Intent intent;
		
		switch (v.getId()) {
		case R.id.main_dp_logo_imageView:
			intent = new Intent(MainActivity.this, DpTabActivity.class);
			startActivity(intent);
			break;
		case R.id.main_button_memo_imageView:
			intent = new Intent(MainActivity.this, MemoTabActivity.class);
			startActivity(intent);
			break;
		case R.id.main_button_setting_imageView:
			intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.main_button_scrap_Button:
			intent = new Intent(MainActivity.this, ScrapListActivity.class);
			startActivity(intent);
			break;
		case R.id.main_button_document_Button:
			intent = new Intent(MainActivity.this, DocumentListActivity.class);
			startActivity(intent);
			break;
		case R.id.main_button_comment_Button:
			intent = new Intent(MainActivity.this, CommentListActivity.class);
			startActivity(intent);
			break;
		}
		
	}
    
}
