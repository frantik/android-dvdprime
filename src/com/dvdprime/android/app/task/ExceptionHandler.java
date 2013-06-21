package com.dvdprime.android.app.task;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.activity.BaseActivity;
import com.dvdprime.android.app.constants.MessageHelper;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.http.ServerException;

/**
 * ExceptionHandler Ŭ������ ���� ó���� ���.
 */
public class ExceptionHandler {
	
	/** The instance. */
	private static ExceptionHandler instance;
	
	/**
	 * ���� ȹ�濩�� Ȯ�κ���
	 */
	private boolean haveEx = false;
	
	private ExceptionHandler() {
		
	}
	
	public static ExceptionHandler getInstance() {
		if (instance == null) {
			instance = new ExceptionHandler();
		}
		return instance;
	}
	
	public void handle(boolean isCancelled, boolean quit, Exception... exceptions){
		if (exceptions != null) {
			Throwable e = exceptions[0];
			final Activity activity = ContextHolder.getInstance().getCurrentActivity();
			if (MessageHelper.DEBUG)
				Log.d("DP", Log.getStackTraceString(e));
			if (e instanceof ServerException || e instanceof IllegalStateException) {
				if (!isCancelled && !haveEx) {
					haveEx = true;
					DialogBuilder.showCriticalErrorMessageDialog(activity, null, quit, R.string.server_problem);
				}
			} else if (e instanceof IOException || e instanceof SocketTimeoutException 
						|| e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
				if (!isCancelled && !haveEx) {
					haveEx = true;
					// ��õ��� ������ ��Ƽ��Ƽ �� ��� ���̾�α׸� ��õ������� �����ش�.
					if (activity instanceof BaseActivity && ((BaseActivity)activity).isRetry()) {
						DialogBuilder.createConfirmDialog(activity, activity.getString(R.string.dialog_title_network_error), 
								activity.getString(R.string.retry_network_problem), 
								R.string.button_yes,
								((BaseActivity)activity).getRetryType() == MessageHelper.RETRY_GET	// ����� ��� [�ݱ�]�� ǥ��
								?R.string.button_close:R.string.button_no,							// �� �� �۾��� [�ƴϿ�]�� ǥ��
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(final DialogInterface dialog, final int which) {
										dialog.dismiss();
										haveEx = false;
										((BaseActivity)activity).execRetry();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(final DialogInterface dialog, final int which) {
										dialog.dismiss();
										haveEx = false;
										// ����̳� ������ �������� ��� ���������� ������.
										if (((BaseActivity)activity).getRetryType() == MessageHelper.RETRY_GET)
											activity.finish();
										// ���̳� ��� �ۼ��� �Ǵ� ������
										// �������� ��� ��ҹ�ư�� �����ϸ� ���̾�α׸� ����
										else {
											dialog.dismiss();
											haveEx = false;
										}
									}
								}).show();
					} else {
						DialogBuilder.showCriticalErrorMessageDialog(activity, null, quit, R.string.network_problem);
					}
				}
			} else if (e instanceof MalformedURLException || e instanceof StringIndexOutOfBoundsException) {
				if (!isCancelled && !haveEx) {
					haveEx = true;
					DialogBuilder.showCriticalErrorMessageDialog(activity, null, quit, R.string.server_problem);
				}
			} else if (e instanceof OutOfMemoryError) {
				System.gc();
			} else {
				if (!isCancelled) {
					Toast.makeText(ContextHolder.getInstance().getContext(), exceptions[0].toString(), Toast.LENGTH_SHORT).show();
				}
			}
		}	
	}
	
	public void clearHaveEx() {
		haveEx = false;
	}
}
