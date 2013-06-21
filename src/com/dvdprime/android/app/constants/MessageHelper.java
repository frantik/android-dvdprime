package com.dvdprime.android.app.constants;

import com.dvdprime.android.app.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MessageHelper {
    /** 디버그 로그 출력 여부 */
    public final static boolean DEBUG = false;
    
    // Retry values
    public static final int RETRY_INIT		= 0;
    public static final int RETRY_GET		= 1;
    public static final int RETRY_WRITE		= 2;
    public static final int RETRY_DELETE	= 3;
    
    // Memo values
    public static final int MEMO_R = 1;
    public static final int MEMO_S = 2;
    public static final int MEMO_X = 3;
	
    public static final void clickedLinkAction(Activity mActivity, String url) {
		Intent intent;
		if (DEBUG)
			Log.d("P", "clicked link : " + url);
		
		if (url != null) {
			if (url.startsWith("http://")	// 웹페이지 보기
						|| url.startsWith("geo:") // 구글맵 보기 
						|| url.startsWith("http://maps.google.com/maps?")	// 구글 길찾기
							// http://maps.google.com/maps?f=d&saddr=출발지주소&daddr=도착지주소&hl=ko
			) 
			{
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				mActivity.startActivity(intent);
			}
			else if (url.startsWith("tel:")) { // 전화걸기
				intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
				mActivity.startActivity(intent);
			}
			else if (url.startsWith("tel:")) { // 전화걸기로 넘김
				intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
				mActivity.startActivity(intent);
			}
			else if (url.startsWith("smsto:")) { // SMS 발송
				intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
				intent.putExtra("sms_body", ""); // SMS Text
				mActivity.startActivity(intent);
			}
			else if (url.startsWith("content:")) { // MMS 발송 (content://media/external/images/media/23)
				intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra("sms_body", ""); // MMS Text
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
				intent.setType("image/png");
				mActivity.startActivity(intent);
			}
			//else if (url.startsWith("mailto:")) { // 이메일 발송
			else if (StringUtil.isValidEmail(url)) {
				intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/email");
				intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{url}); 
				mActivity.startActivity(intent);
			}
			else if (url.startsWith("market://search?")) { // 마켓 검색
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				mActivity.startActivity(intent);
			}
			else if (url.startsWith("market://details?id=")) { // 마켓 상세 화면
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				mActivity.startActivity(intent);
			}
			
			/*
			 * 그외 여러가지 인텐트 호출
			 */
			
			// 미디어파일 플레이 하기
//			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file//sdcard/song.mp3"));
//			intent.setDataAndType(Uri.parse("file://sdcard/song.mp3"), "audio/mp3");
//			mActivity.startActivity(intent);
			
			// 설치 어플 제거
//			intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", "strPackageName", null));
//			mActivity.startActivity(intent);
			
			// 구글 검색
//			intent = new Intent();
//			intent.setAction(Intent.ACTION_WEB_SEARCH);
//			intent.putExtra(SearchManager.QUERY, "searchString");
//			mActivity.startActivity(intent);			
		}
    }
}
