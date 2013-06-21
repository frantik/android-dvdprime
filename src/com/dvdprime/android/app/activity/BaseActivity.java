package com.dvdprime.android.app.activity;

import com.dvdprime.android.app.BaseApplication;
import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.manager.ImageManager;
import com.dvdprime.android.app.task.DataConsumer;
import com.dvdprime.android.app.task.DataEvent;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * ���ø����̼��� activity ���� Ŭ������ ����Ѵ�.
 *
 * @author Kwang-myung,Choi (frantik@gmail.com)
 */
public abstract class BaseActivity extends Activity implements DataConsumer {
	
	private static final String TAG = "BaseActivity";
	
	/** The content view resource id. */
	protected int contentView;

	/** application ��ü */
	protected BaseApplication application;
	
	/** The loading flag, used for display loading wheel. */
	private boolean loading;
	
	/** The loading flag, used for display loading wheel. */
	private boolean progressVisible = false;
	
	/** The retry flag when an network error occurs */
	private boolean retry = false;
	
	/**
	 * retry type
	 * 1 : get list or data -> load();
	 * 2 : write -> 
	 * 3 : delete ->
	 * {@link:MessageHelper.RETRY_*} 
	 */
	private int rType = 0;
	
	private ProgressDialog mLoadingDialog;
	private Dialog mLoginDialog;
	
	// TODO
	// ����� ��ҽ� �۾����� �����ؾ���.
	DialogInterface.OnCancelListener cancelListener =
        new DialogInterface.OnCancelListener() {

        public void onCancel(DialogInterface dialog) {
        	if (MessageHelper.DEBUG)
        		Log.d(TAG,"cancelListener onCancel");
        }
    };
	
	public BaseActivity(final int pContentView) {
		super();
		this.contentView = pContentView;
	}
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (BaseApplication) getApplication();
		initHeader();
		loading = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// ���� Avtivity nulló��.
		ContextHolder.getInstance().setCurrentActivity(null);
		
		// ��ϵ� ������ ������ ����.
		invalidate();
	}
	
	/**
	 * �ʿ��� �����͸� �ε� ����
	 */
	@Override
	protected void onResume() {
		super.onResume();
		ContextHolder.getInstance().setCurrentActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		application = null;
		super.onDestroy();
	}
	
	protected void initHeader() {
		setContentView(contentView);
	}
	
	protected void cencel() {
		// TODO
		// ����� �۾���� �����ؾ���.
	}
	
	@Override
	public final void setContentView(final int layoutResID) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(layoutResID);
		contentView = layoutResID;
	}
	
	@Override
	public void handleEvent(final DataEvent event) {
		loading = false;
		hideLoadingIndicator(false);
	}
	
	@Override
	public void invalidate() {
		
		// �ٿ�ε� �̹��� �۾� ���.
		ImageManager.getInstance().cancel();

		// �ε����̾�α� ����
		hideLoadingIndicator(true);
		
		if(mLoginDialog != null) mLoginDialog.dismiss();
	}
	
	/** Method for load data */
	public void load() {
    	if (MessageHelper.DEBUG)
    		Log.d(TAG, "load()");
		
		loading = true;
	}
	
	/**
	 * Show toast message. 
	 * Used for exception handling
	 * 
	 * @param message the message
	 */
	public final void showToastMessage(final String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	protected final void showLoadingIndicator() {
		
		Activity activity = this;
		
		if(getParent() != null){
			activity = getParent();
		}
		
		if (!progressVisible && isLoading()) {
			if (mLoadingDialog == null) {
				mLoadingDialog = new ProgressDialog(activity);
				mLoadingDialog.setMessage(activity.getText(R.string.bbs_progressbar_loading));
				mLoadingDialog.setIndeterminate(true);
				mLoadingDialog.setCancelable(true);
				mLoadingDialog.show();
				progressVisible = true;
			}
		}
	}
	
	protected final void showLoadingIndicator(int resId) {
		
		Activity activity = this;
		
		if(getParent() != null){
			activity = getParent();
		}
		
		if (!progressVisible && isLoading()) {
			if (mLoadingDialog == null) {
				mLoadingDialog = new ProgressDialog(activity);
				mLoadingDialog.setMessage(activity.getText(resId));
				mLoadingDialog.setIndeterminate(true);
				mLoadingDialog.setCancelable(true);
				mLoadingDialog.show();
				progressVisible = true;
			}
		} else if (mLoadingDialog != null) {
			mLoadingDialog.setMessage(activity.getText(resId));
		}
	}
	
	/**
	 * �ε����̾�α� ����
	 * @param force	�������� ����
	 */
	protected final void hideLoadingIndicator(final boolean force) {
		if (force || (progressVisible && !isLoading())) {
			if (mLoadingDialog != null) {
		    	mLoadingDialog.dismiss();
		        mLoadingDialog = null;
		        progressVisible = false;
		    }
		}
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	public void execRetry() {
		Activity activity = ContextHolder.getInstance().getCurrentActivity();
		switch (rType) {
			case MessageHelper.RETRY_GET: // get list or data
				if (activity instanceof ArticleListActivity)
					((ArticleListActivity) activity).load();
				else if (activity instanceof ArticleContentActivity)
					((ArticleContentActivity) activity).load();
				else if (activity instanceof ScrapListActivity)
					((ScrapListActivity) activity).load();
				break;
			case MessageHelper.RETRY_WRITE: // put data
//				if (activity instanceof ArticleContentActivity)
//					((ArticleContentActivity) activity).commentWrite();
				break;
		}
	}

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(boolean retry, int type) {
		this.retry = retry;
		this.rType = type;
	}
	
	public int getRetryType() {
		return rType;
	}
}