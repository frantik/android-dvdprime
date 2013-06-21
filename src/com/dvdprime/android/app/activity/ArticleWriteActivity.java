package com.dvdprime.android.app.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.CurrentInfo;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.constants.RequestCode;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.dialog.ISetSelectValue;
import com.dvdprime.android.app.manager.AccountManager;
import com.dvdprime.android.app.manager.ArticleManager;
import com.dvdprime.android.app.task.DataEvent;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.ImageUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;
import com.dvdprime.android.app.util.SystemUtil;

public class ArticleWriteActivity extends BaseActivity implements OnClickListener {

	private Activity activity;
	private InputMethodManager mIMManager;
	
	private EditText mSubjectText;
	private EditText mContentText;
	private ImageView mAttachImage;
	private Button mSendButton;
	
	private PrefUtil prefs;
	
	private int attachImgCnt = 0;
	private final int ATTACH_MAX = 1;
	private String mode;
	private String bbsId;
	private String bbsDate;
	private StringBuffer mAttachString = new StringBuffer();

    private String AUTH_CHECK;
    private String FROM_ATTACH = "from_attach"; // 사진첨부로부터 로그인
    private String FROM_WRITE  = "from_write";  // 글쓰기로부터 로그인

    private static Uri sUri;
	private static final String SD_CARD = Const.SDCARD_DIRECTORY;
	
	public ArticleWriteActivity() {
		super(R.layout.article_write_layout);
	}
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mode = extras.getString(IntentKeys.WRITE_MODE);
			bbsId = extras.getString(IntentKeys.BBS_ID);
			bbsDate = extras.getString(IntentKeys.BBS_DATE);
		}
		
		if (StringUtil.isEmpty(mode))
			setTitle(R.string.article_write);
		else
			setTitle(R.string.article_modify);

		activity = this;
		prefs = PrefUtil.getInstance();
		mIMManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mSubjectText = (EditText)findViewById(R.id.article_write_title_editText);
		mSubjectText.addTextChangedListener(mSubjectWatcher);
		mContentText = (EditText)findViewById(R.id.article_write_content_editText);
		mContentText.addTextChangedListener(mContentWatcher);
		
		mSendButton = (Button)findViewById(R.id.article_write_softkey_left_button);
		mSendButton.setOnClickListener(this);
		
		Button mCancelButton = (Button)findViewById(R.id.article_write_softkey_right_button);
		mCancelButton.setOnClickListener(this);
		
		mAttachImage = (ImageView)findViewById(R.id.article_write_attach_imageView);
		mAttachImage.setOnClickListener(this);

		SendButtonSetEnabled(false);
		
		if (StringUtil.isNotEmpty(mode)) { // 수정 모드일 경우 DB에서 데이터를 가져와 기본 입력해준다.
			Cursor c = DpUtil.query(this, DpDB.Content.CONTENT_URI, null, null, null, null);
			if (c != null && c.moveToFirst()) {
				String subject = StringUtil.decode(c.getString(c.getColumnIndexOrThrow(DpDB.Content.CTT_TITLE)));
				if (StringUtil.contains(subject, "] "))
					subject = StringUtil.substringAfter(subject, "] ");
				String content = StringUtil.decode(c.getString(c.getColumnIndexOrThrow(DpDB.Content.CTT_CONTENT)));
		        if (StringUtil.equals(prefs.getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
		        	content = StringUtil.substringAfter(content, "<font color='#dddddd'>");
		        	content = StringUtil.substringBeforeLast(content, "</body>");
		        	content = StringUtil.replace(content, Const.HOMEPAGE_URL +"/dpUserUpImg/", "/dpUserUpImg/");
		        	content = StringUtil.replace(content, "<br/>", "\n");
		        	content = StringUtil.replace(content, "<BR>", "\n");
		        } else {
		        	content = StringUtil.substringAfter(content, "<body>");
		        	content = StringUtil.substringBeforeLast(content, "</body>");
		        	content = StringUtil.replace(content, Const.HOMEPAGE_URL +"/dpUserUpImg/", "/dpUserUpImg/");
		        	content = StringUtil.replace(content, Const.HOMEPAGE_URL +"<div id=\"DWMCOLOR\" style=\"background-color:#ffffff;\">", "");
		        	content = StringUtil.replace(content, Const.HOMEPAGE_URL +"<div id=\"DWMCOLOR\" style=\"background-color:#FFFFFF;\">", "");
		        	content = StringUtil.replace(content, "<br/>", "\n");
		        	content = StringUtil.replace(content, "<BR>", "\n");
		        }

				mSubjectText.setText(subject);
	    		mContentText.setText(content);
			}
			if (c != null)
		        // Cursor Close
		        c.close();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (StringUtil.isEmpty(prefs.getString(PreferenceKeys.ACCOUNT_ID, null)))
			DialogBuilder.showCriticalErrorMessageDialog(this, 
					null, true, R.string.dialog_write_not_auth_message);
	}

	@Override
	protected void onPause() {
		
		ArticleManager.getInstance().removeConsumer();
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		
		ArticleManager.getInstance().removeConsumer();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Take Photo
		if( requestCode == RequestCode.REQ_CODE_TAKE_PHOTO )
		{
			if( resultCode == Activity.RESULT_OK )
			{	
				if(sUri != null) {
					if (attachImgCnt++ < ATTACH_MAX) {
						// 사진첨부 전송
						showLoadingIndicator(R.string.msg_upload_photo);
						new AttachedPhotoTask().execute();
					} else {
						Toast.makeText(getApplicationContext(),getString(R.string.exceed_max_attach_image), Toast.LENGTH_SHORT).show();
						sUri = null;
					}
				} else {
					Toast.makeText(getApplicationContext(),getString(R.string.do_not_access_photo_data), Toast.LENGTH_SHORT).show();
					sUri = null;
				}
			}
		}
		// Get a Photo
		else if( requestCode == RequestCode.REQ_CODE_PICK_PICTURE ) 
		{
			if( resultCode == Activity.RESULT_OK ) {
				// Selected Image Path
				sUri = data.getData();
				if (sUri != null) {
					if (attachImgCnt++ < ATTACH_MAX) {
						// 사진첨부 전송
						showLoadingIndicator(R.string.msg_upload_photo);
						new AttachedPhotoTask().execute();
						mAttachImage.setVisibility(View.GONE);
					} else {
						Toast.makeText(getApplicationContext(),getString(R.string.exceed_max_attach_image), Toast.LENGTH_SHORT).show();
						sUri = null;
					}
				} else {
					Toast.makeText(getApplicationContext(),getString(R.string.do_not_access_photo_data), Toast.LENGTH_SHORT).show();
					sUri = null;
				}
			}
		}
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
					if (StringUtil.equals(AUTH_CHECK, FROM_ATTACH))
						onArticleAttachUpload();
					else if (StringUtil.equals(AUTH_CHECK, FROM_WRITE)) {
						if (StringUtil.isNotEmpty(mode))
							onArticleModify();
						else
							onArticleWrite();
					}
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
					if (StringUtil.equals(AUTH_CHECK, FROM_ATTACH))
						onArticleAttachUpload();
					else if (StringUtil.equals(AUTH_CHECK, FROM_WRITE)) {
						if (StringUtil.isNotEmpty(mode))
							onArticleModify();
						else
							onArticleWrite();
					}
				break;
			case DataEvent.ARTICLE_WRITE:
			case DataEvent.ARTICLE_MODIFY:
				if ((Boolean)event.getData()) {
					Toast.makeText(getBaseContext(), getString(R.string.update_success), Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					hideLoadingIndicator(true);
					finish();
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.update_fail), Toast.LENGTH_SHORT).show();
					hideLoadingIndicator(true);
				}
				break;
			case DataEvent.ARTICLE_ADD_IMAGE:
				if ((String)event.getData() != null) {
					String fResult = String.format(getString(R.string.add_photo_result), (String)event.getData());
					mContentText.append(fResult);
					mAttachString.append((String)event.getData());//.append("|");
					mAttachImage.setVisibility(View.GONE);
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.msg_upload_fail), Toast.LENGTH_SHORT).show();
				}
				hideLoadingIndicator(true);
				break;
			}
		} else {
			hideLoadingIndicator(true);
			super.handleEvent(event);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_write_softkey_left_button:
			closeSoftKeyboard();
			AUTH_CHECK = FROM_WRITE;
			onLoginCheck();
			break;
		case R.id.article_write_softkey_right_button:
			closeSoftKeyboard();
			finish();
			break;
		case R.id.article_write_attach_imageView:
			if (SystemUtil.isExternalWritable()) {
				closeSoftKeyboard();
				String[] items = {getString(R.string.attach_take_photo), getString(R.string.attach_photo_album)};
				DialogBuilder.createSelectDialog(activity, getString(R.string.attach_photo), items, 0, 
						new ISetSelectValue(){
							@Override
							public void setValue(String value, int index) {
								if (index == 0) {
									File sdDir = new File(SD_CARD);
									if( !sdDir.exists() )
									{	
										Toast.makeText(getApplicationContext(),getString(R.string.image_not_ready_sdcard), Toast.LENGTH_SHORT).show();
										return;
									}
									
									File temp = new File(sdDir + File.separator + System.currentTimeMillis() + ".jpg");
									if(temp.exists()) temp.delete();
									sUri = Uri.fromFile(temp);
									
									Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
									i.putExtra(MediaStore.EXTRA_OUTPUT, sUri);
									i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = low quality suitable for MMS
									startActivityForResult(i, RequestCode.REQ_CODE_TAKE_PHOTO );
								} else {
									Intent i = new Intent(Intent.ACTION_PICK);
									i.setType("image/*"); 
//									i.setType(MediaStore.Images.Media.CONTENT_TYPE);
//									i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
									startActivityForResult(i, RequestCode.REQ_CODE_PICK_PICTURE);
								}
							}
						}
				).show();
			} else {
				Toast.makeText(activity, getText(R.string.image_not_ready_sdcard), Toast.LENGTH_SHORT).show();
			}
			break;
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
													false,
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
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 글쓰기 전송
	 */
	private void onArticleWrite() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_WRITE);
			am.write(mSubjectText.getText().toString(), 
					mContentText.getText().toString(), 
					CurrentInfo.BBS_MAJOR, CurrentInfo.BBS_MINOR, CurrentInfo.BBS_MASTER_ID,
					mAttachString.toString());
			showLoadingIndicator(R.string.bbs_progressbar_save);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 글수정 전송
	 */
	private void onArticleModify() {
		if (SystemUtil.isNetworkAvailable(activity)) {
			ArticleManager am = ArticleManager.getInstance();
			am.addConsumer(this, DataEvent.ARTICLE_MODIFY);
			am.modify(mSubjectText.getText().toString(), 
					mContentText.getText().toString(), 
					CurrentInfo.BBS_MAJOR, CurrentInfo.BBS_MINOR, CurrentInfo.BBS_MASTER_ID,
					bbsId, bbsDate, mAttachString.toString());
			showLoadingIndicator(R.string.bbs_progressbar_save);
		} else {
			DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
													false,
													R.string.network_problem);													
		}
	}
	
	/**
	 * 첨부 사진 전송
	 */
	private void onArticleAttachUpload() {
		if (sUri != null) { 
			if (SystemUtil.isNetworkAvailable(activity)) {
				ArticleManager am = ArticleManager.getInstance();
				am.addConsumer(this, DataEvent.ARTICLE_ADD_IMAGE);
				am.upload(sUri);
			} else {
				DialogBuilder.showCriticalErrorMessageDialog(activity, R.string.dialog_title_network_error,
														false,
														R.string.network_problem);													
			}
		} else {
			hideLoadingIndicator(true);
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

	private TextWatcher mSubjectWatcher = new TextWatcher() {

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

        	int length = s.length();
			final int SUBJECT_MAX_LENGTH = 100;
        	if(length > SUBJECT_MAX_LENGTH) { //exceed 100 chars in a message
        		mSubjectText.setText(s.subSequence(0, SUBJECT_MAX_LENGTH-1));

        		Toast.makeText(activity, getString(R.string.exceed_max_subject_length), Toast.LENGTH_SHORT).show();
        		mSendButton.setEnabled(false);
        	}
		}

        public void afterTextChanged(Editable s) { }
    };
    
	private TextWatcher mContentWatcher = new TextWatcher() {

		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			onUserInteraction();
            
			if (mSubjectText.getText().length() > 0) {
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
    
	/**
	 * bitmap 읽어옴.
	 * 
	 * @param imgUri
	 * @return
	 */
	private Bitmap loadImage(Uri imgUri) {
		Bitmap bitmap = null;

		try {
			if (imgUri != null && imgUri.getPath().length() != 0) {
				bitmap = Media.getBitmap(getContentResolver(), imgUri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	class AttachedPhotoTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... v) {

			String path = "";
			int orientation = 0;

			if (sUri.toString().contains("content:")) {
				String[] projection;
				Uri imgPath;

				projection = new String[] { Images.Media._ID,
						Images.Media.DATA, Images.Media.MIME_TYPE,
						Images.Media.ORIENTATION };

				imgPath = sUri;

				Cursor c = getContentResolver().query(imgPath, projection, null, null, null);
				String thumbData = "";

				if (c.moveToFirst()) {
					int dataColumn, /*mimeTypeColumn,*/ orientationColumn;

					dataColumn = c.getColumnIndex(Images.Media.DATA);
					orientationColumn = c.getColumnIndex(Images.Media.ORIENTATION);

					orientation = c.getInt(orientationColumn);
					thumbData = c.getString(dataColumn);
					path = thumbData;
				}
			} else { // file is not in media library
				path = sUri.toString().replace("file://", "");
			}
			
			orientation = ImageUtil.getExifRotation(path);

			if (orientation != 0) {
				Matrix mtx = new Matrix();
				mtx.postRotate(orientation);

				Bitmap bitmap = loadImage(sUri);
				// java.lang.OutOfMemoryError: bitmap size exceeds VM budget
				// 오류 발생으로 인하여 이미지 사이지를 사전에 줄여줌.
				if (bitmap.getWidth() > 1024) {
					bitmap = ImageUtil.ResizeBitmap(bitmap, 1024);
				}
				Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), mtx, true);

				try {
					File tmpPath = getFileStreamPath("temp.jpg");
					tmpPath.delete();

					OutputStream outStream = openFileOutput("temp.jpg", 0);
					rotatedBMP.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
					outStream.flush();
					outStream.close();

					sUri = Uri.fromFile(tmpPath);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				bitmap = null;
				rotatedBMP = null;
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void v) {
			AUTH_CHECK = FROM_ATTACH;
			onLoginCheck();
		}
	}

}
