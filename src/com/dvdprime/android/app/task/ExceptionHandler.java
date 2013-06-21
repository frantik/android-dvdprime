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
 * ExceptionHandler 클래스는 예외 처리에 사용.
 */
public class ExceptionHandler {
	
	/** The instance. */
	private static ExceptionHandler instance;
	
	/**
	 * 예외 획득여부 확인변수
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
					// 재시도를 설정한 액티비티 일 경우 다이얼로그를 재시도용으로 보여준다.
					if (activity instanceof BaseActivity && ((BaseActivity)activity).isRetry()) {
						DialogBuilder.createConfirmDialog(activity, activity.getString(R.string.dialog_title_network_error), 
								activity.getString(R.string.retry_network_problem), 
								R.string.button_yes,
								((BaseActivity)activity).getRetryType() == MessageHelper.RETRY_GET	// 목록의 경우 [닫기]로 표시
								?R.string.button_close:R.string.button_no,							// 그 외 작업은 [아니오]로 표시
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
										// 목록이나 정보를 가져오는 경우 실패했을때 종료함.
										if (((BaseActivity)activity).getRetryType() == MessageHelper.RETRY_GET)
											activity.finish();
										// 글이나 댓글 작성시 또는 삭제시
										// 실패했을 경우 취소버튼을 선택하면 다이얼로그만 닫음
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
